package com.yfy.basearchitecture.core.datastore.api.exceptions

sealed class DataStoreException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    class SerializationException(message: String, cause: Throwable? = null) : 
        DataStoreException("Serialization failed: $message", cause)
    
    class DeserializationException(message: String, cause: Throwable? = null) : 
        DataStoreException("Deserialization failed: $message", cause)
    
    class CacheException(message: String, cause: Throwable? = null) : 
        DataStoreException("Cache operation failed: $message", cause)
    
    class PreferenceException(message: String, cause: Throwable? = null) : 
        DataStoreException("Preference operation failed: $message", cause)
    
    class MigrationException(message: String, cause: Throwable? = null) : 
        DataStoreException("DataStore migration failed: $message", cause)
} 