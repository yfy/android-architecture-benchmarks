package com.yfy.basearchitecture.core.datastore.api.models

import kotlinx.serialization.Serializable

@Serializable
data class CacheEntry<T>(
    val value: T,
    val timestamp: Long = 0L,
    val ttlMillis: Long? = null
) {
    fun isExpired(): Boolean {
        return ttlMillis?.let { 
            System.currentTimeMillis() - timestamp > it 
        } ?: false
    }
}

@Serializable
data class CacheInfo(
    val totalEntries: Int,
    val totalSizeBytes: Long,
    val oldestEntryTimestamp: Long,
    val newestEntryTimestamp: Long
) 