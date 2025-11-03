package com.yfy.basearchitecture.core.datastore.api.interfaces

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface CacheManager {

    // Cache with TTL (Time To Live)
    suspend fun <T> cache(
        key: String,
        value: T,
        serializer: kotlinx.serialization.KSerializer<T>,
        ttl: Duration? = null
    )

    // Get cached value
    fun <T> getCached(
        key: String,
        serializer: kotlinx.serialization.KSerializer<T>
    ): Flow<T?>

    // Check if cached value is valid (not expired)
    suspend fun isCacheValid(key: String): Boolean

    // Clear expired cache entries
    suspend fun clearExpiredCache()

    // Clear all cache
    suspend fun clearAllCache()

    // Get cache size
    suspend fun getCacheSize(): Long

    // Set cache size limit
    suspend fun setCacheSizeLimit(sizeInBytes: Long)
}