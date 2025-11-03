package com.yfy.basearchitecture.core.datastore.impl.managers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.yfy.basearchitecture.core.datastore.api.exceptions.DataStoreException
import com.yfy.basearchitecture.core.datastore.api.interfaces.PreferenceManager
import com.yfy.basearchitecture.core.datastore.impl.di.PreferencesDataStore
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getAllKeysFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getBooleanFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getFloatFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getIntFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getLongFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getStringFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.getStringSetFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.DataStoreUtils.hasKeyFlow
import com.yfy.basearchitecture.core.datastore.impl.utils.SerializationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManagerImpl @Inject constructor(
    @PreferencesDataStore private val dataStore: DataStore<Preferences>
) : PreferenceManager {

    override suspend fun setString(key: String, value: String) {
        try {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set string preference: $key")
            throw DataStoreException.PreferenceException("Failed to set string preference: $key", e)
        }
    }

    override fun getString(key: String, defaultValue: String): Flow<String> {
        return dataStore.data.getStringFlow(key, defaultValue)
    }

    override suspend fun setInt(key: String, value: Int) {
        try {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set int preference: $key")
            throw DataStoreException.PreferenceException("Failed to set int preference: $key", e)
        }
    }

    override fun getInt(key: String, defaultValue: Int): Flow<Int> {
        return dataStore.data.getIntFlow(key, defaultValue)
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set boolean preference: $key")
            throw DataStoreException.PreferenceException("Failed to set boolean preference: $key", e)
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Flow<Boolean> {
        return dataStore.data.getBooleanFlow(key, defaultValue)
    }

    override suspend fun setLong(key: String, value: Long) {
        try {
            dataStore.edit { preferences ->
                preferences[longPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set long preference: $key")
            throw DataStoreException.PreferenceException("Failed to set long preference: $key", e)
        }
    }

    override fun getLong(key: String, defaultValue: Long): Flow<Long> {
        return dataStore.data.getLongFlow( key, defaultValue)
    }

    override suspend fun setFloat(key: String, value: Float) {
        try {
            dataStore.edit { preferences ->
                preferences[floatPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set float preference: $key")
            throw DataStoreException.PreferenceException("Failed to set float preference: $key", e)
        }
    }

    override fun getFloat(key: String, defaultValue: Float): Flow<Float> {
        return dataStore.data.getFloatFlow(key, defaultValue)
    }

    override suspend fun <T> setObject(key: String, value: T, serializer: KSerializer<T>) {
        try {
            val serializedValue = SerializationUtils.serialize(value, serializer)
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = serializedValue
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set object preference: $key")
            throw DataStoreException.PreferenceException("Failed to set object preference: $key", e)
        }
    }

    override fun <T> getObject(key: String, serializer: KSerializer<T>, defaultValue: T): Flow<T> {
        return dataStore.data.getStringFlow(key, "")
            .catch { exception ->
                Timber.e(exception, "Error reading object preference: $key")
                emit("")
            }
            .map { stringValue ->
                when {
                    stringValue.isBlank() -> defaultValue
                    else -> {
                        try {
                            SerializationUtils.deserialize(stringValue, serializer)
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to deserialize object preference: $key")
                            defaultValue
                        }
                    }
                }
            }
    }

    override suspend fun setStringSet(key: String, value: Set<String>) {
        try {
            dataStore.edit { preferences ->
                preferences[stringSetPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set string set preference: $key")
            throw DataStoreException.PreferenceException("Failed to set string set preference: $key", e)
        }
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Flow<Set<String>> {
        return dataStore.data.getStringSetFlow(key, defaultValue)
    }

    override suspend fun remove(key: String) {
        try {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove preference: $key")
            throw DataStoreException.PreferenceException("Failed to remove preference: $key", e)
        }
    }

    override suspend fun clearAll() {
        try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear all preferences")
            throw DataStoreException.PreferenceException("Failed to clear all preferences", e)
        }
    }

    override suspend fun setBulk(preferences: Map<String, Any>) {
        try {
            dataStore.edit { mutablePreferences ->
                preferences.forEach { (key, value) ->
                    when (value) {
                        is String -> mutablePreferences[stringPreferencesKey(key)] = value
                        is Int -> mutablePreferences[intPreferencesKey(key)] = value
                        is Boolean -> mutablePreferences[booleanPreferencesKey(key)] = value
                        is Long -> mutablePreferences[longPreferencesKey(key)] = value
                        is Float -> mutablePreferences[floatPreferencesKey(key)] = value
                        is Set<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            mutablePreferences[stringSetPreferencesKey(key)] = value as Set<String>
                        }
                        else -> {
                            Timber.w("Unsupported preference type for key: $key, value: $value")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set bulk preferences")
            throw DataStoreException.PreferenceException("Failed to set bulk preferences", e)
        }
    }

    override fun hasKey(key: String): Flow<Boolean> {
        return dataStore.data.hasKeyFlow(key)
    }

    override fun getAllKeys(): Flow<Set<String>> {
        return dataStore.data.getAllKeysFlow()
    }
}