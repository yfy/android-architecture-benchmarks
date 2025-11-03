package com.yfy.basearchitecture.core.datastore.impl.utils

import com.yfy.basearchitecture.core.datastore.api.exceptions.DataStoreException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Utility class for serialization operations using kotlinx.serialization
 */
object SerializationUtils {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    /**
     * Serialize an object to JSON string
     */
    fun <T> serialize(obj: T, serializer: KSerializer<T>): String {
        return try {
            json.encodeToString(serializer, obj)
        } catch (e: DataStoreException.SerializationException) {
            Timber.e(e, "Failed to serialize object")
            throw DataStoreException.SerializationException("Failed to serialize object", e)
        }
    }

    /**
     * Deserialize a JSON string to object
     */
    fun <T> deserialize(jsonString: String, serializer: KSerializer<T>): T {
        return try {
            json.decodeFromString(serializer, jsonString)
        } catch (e: DataStoreException.SerializationException) {
            Timber.e(e, "Failed to deserialize object")
            throw DataStoreException.DeserializationException("Failed to deserialize object", e)
        }
    }
}