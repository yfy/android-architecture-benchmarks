package com.yfy.basearchitecture.core.datastore.impl.managers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yfy.basearchitecture.core.datastore.api.exceptions.DataStoreException
import com.yfy.basearchitecture.core.datastore.api.interfaces.CacheManager
import com.yfy.basearchitecture.core.datastore.api.models.CacheEntry
import com.yfy.basearchitecture.core.datastore.impl.di.CacheDataStore
import com.yfy.basearchitecture.core.datastore.impl.utils.SerializationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class CacheManagerImpl @Inject constructor(
    @CacheDataStore private val dataStore: DataStore<Preferences>
) : CacheManager {

    companion object {
        private const val CACHE_SIZE_LIMIT_KEY = "cache_size_limit"
        private const val DEFAULT_CACHE_SIZE_LIMIT = 50 * 1024 * 1024L // 50MB
    }

    override suspend fun <T> cache(
        key: String,
        value: T,
        serializer: KSerializer<T>,
        ttl: Duration?
    ) {
        try {
            val cacheEntry = CacheEntry(
                value = value,
                timestamp = System.currentTimeMillis(),
                ttlMillis = ttl?.inWholeMilliseconds
            )

            val serializedEntry = SerializationUtils.serialize(
                cacheEntry,
                CacheEntry.serializer(serializer)
            )

            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = serializedEntry
            }

            // Clean up expired entries periodically
            cleanupExpiredEntries()

        } catch (e: Exception) {
            Timber.e(e, "Failed to cache value for key: $key")
            throw DataStoreException.CacheException("Failed to cache value for key: $key", e)
        }
    }

    override fun <T> getCached(key: String, serializer: KSerializer<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            val serializedEntry = preferences[stringPreferencesKey(key)]
            if (serializedEntry != null) {
                try {
                    val cacheEntry = SerializationUtils.deserialize(
                        serializedEntry,
                        CacheEntry.serializer(serializer)
                    )

                    if (cacheEntry.isExpired()) {
                        // Remove expired entry
                        dataStore.edit { it.remove(stringPreferencesKey(key)) }
                        null
                    } else {
                        cacheEntry.value
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to deserialize cache entry for key: $key")
                    null
                }
            } else {
                null
            }
        }.catch { exception ->
            Timber.e(exception, "Error reading cache for key: $key")
            emit(null)
        }
    }

    override suspend fun isCacheValid(key: String): Boolean {
        return try {
            dataStore.data.map { preferences ->
                val serializedEntry = preferences[stringPreferencesKey(key)]
                if (serializedEntry != null) {
                    try {
                        val cacheEntry = SerializationUtils.deserialize(
                            serializedEntry,
                            CacheEntry.serializer(serializer<Any>())
                        )
                        !cacheEntry.isExpired()
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    false
                }
            }.first()
        } catch (e: Exception) {
            Timber.e(e, "Error checking cache validity for key: $key")
            false
        }
    }

    override suspend fun clearExpiredCache() {
        try {
            dataStore.edit { preferences ->
                val keysToRemove = mutableListOf<Preferences.Key<String>>()

                preferences.asMap().forEach { (key, value) ->
                    if (value is String) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val stringKey = key as Preferences.Key<String>
                            val cacheEntry = SerializationUtils.deserialize(
                                value,
                                CacheEntry.serializer(serializer<Any>())
                            )

                            if (cacheEntry.isExpired()) {
                                keysToRemove.add(stringKey)
                            }
                        } catch (e: Exception) {
                            // If we can't deserialize, consider it corrupted and remove
                            @Suppress("UNCHECKED_CAST")
                            keysToRemove.add(key as Preferences.Key<String>)
                        }
                    }
                }

                keysToRemove.forEach { keyToRemove ->
                    preferences.remove(keyToRemove)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear expired cache")
            throw DataStoreException.CacheException("Failed to clear expired cache", e)
        }
    }

    override suspend fun clearAllCache() {
        try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear all cache")
            throw DataStoreException.CacheException("Failed to clear all cache", e)
        }
    }

    override suspend fun getCacheSize(): Long {
        return try {
            dataStore.data.map { preferences ->
                preferences.asMap().values.sumOf { value ->
                    when (value) {
                        is String -> value.toByteArray().size.toLong()
                        else -> 0L
                    }
                }
            }.first()
        } catch (e: Exception) {
            Timber.e(e, "Error calculating cache size")
            0L
        }
    }

    override suspend fun setCacheSizeLimit(sizeInBytes: Long) {
        try {
            dataStore.edit { preferences ->
                preferences[longPreferencesKey(CACHE_SIZE_LIMIT_KEY)] = sizeInBytes
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set cache size limit")
            throw DataStoreException.CacheException("Failed to set cache size limit", e)
        }
    }

    private suspend fun cleanupExpiredEntries() {
        try {
            clearExpiredCache()

            // Check if we need to clean up based on size
            val currentSize = getCacheSize()
            val sizeLimit = getCacheSizeLimit()

            if (currentSize > sizeLimit) {
                // Remove oldest entries until we're under the limit
                cleanupOldestEntries(currentSize - sizeLimit)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during cache cleanup")
        }
    }

    private suspend fun getCacheSizeLimit(): Long {
        return try {
            dataStore.data.map { preferences ->
                preferences[longPreferencesKey(CACHE_SIZE_LIMIT_KEY)] ?: DEFAULT_CACHE_SIZE_LIMIT
            }.catch { DEFAULT_CACHE_SIZE_LIMIT }.let { flow ->
                var result = DEFAULT_CACHE_SIZE_LIMIT
                flow.collect { result = it }
                result
            }
        } catch (e: Exception) {
            DEFAULT_CACHE_SIZE_LIMIT
        }
    }

    private suspend fun cleanupOldestEntries(bytesToRemove: Long) {
        try {
            dataStore.edit { preferences ->
                val entriesWithTimestamp = mutableListOf<Pair<Preferences.Key<String>, Long>>()

                preferences.asMap().forEach { (key, value) ->
                    if (value is String) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val stringKey = key as Preferences.Key<String>
                            val cacheEntry = SerializationUtils.deserialize(
                                value,
                                CacheEntry.serializer(serializer<Any>())
                            )
                            entriesWithTimestamp.add(stringKey to cacheEntry.timestamp)
                        } catch (e: Exception) {
                            // Corrupted entry, mark for removal
                            @Suppress("UNCHECKED_CAST")
                            entriesWithTimestamp.add(key as Preferences.Key<String> to 0L)
                        }
                    }
                }

                // Sort by timestamp (oldest first)
                entriesWithTimestamp.sortBy { it.second }

                var removedBytes = 0L
                for ((key, _) in entriesWithTimestamp) {
                    if (removedBytes >= bytesToRemove) break

                    val value = preferences[key]
                    if (value is String) {
                        removedBytes += value.toByteArray().size
                        preferences.remove(key)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error cleaning up oldest cache entries")
        }
    }
}