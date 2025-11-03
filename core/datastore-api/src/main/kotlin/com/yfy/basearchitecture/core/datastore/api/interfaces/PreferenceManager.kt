package com.yfy.basearchitecture.core.datastore.api.interfaces

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

interface PreferenceManager {

    // String preferences
    suspend fun setString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): Flow<String>

    // Int preferences  
    suspend fun setInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Flow<Int>

    // Boolean preferences
    suspend fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean>

    // Long preferences
    suspend fun setLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Flow<Long>

    // Float preferences
    suspend fun setFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float = 0f): Flow<Float>

    // Complex object preferences (with serialization)
    suspend fun <T> setObject(key: String, value: T, serializer: KSerializer<T>)
    fun <T> getObject(key: String, serializer: KSerializer<T>, defaultValue: T): Flow<T>

    // List preferences
    suspend fun setStringSet(key: String, value: Set<String>)
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>>

    // Clear operations
    suspend fun remove(key: String)
    suspend fun clearAll()

    // Bulk operations
    suspend fun setBulk(preferences: Map<String, Any>)

    // Check if key exists
    fun hasKey(key: String): Flow<Boolean>

    // Get all keys
    fun getAllKeys(): Flow<Set<String>>
}