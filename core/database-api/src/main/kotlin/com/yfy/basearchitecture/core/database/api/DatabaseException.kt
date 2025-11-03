package com.yfy.basearchitecture.core.database.api

sealed class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class ConnectionException(message: String, cause: Throwable? = null) : DatabaseException(message, cause)

    class MigrationException(message: String, cause: Throwable? = null) : DatabaseException(message, cause)

    class QueryException(message: String, cause: Throwable? = null) : DatabaseException(message, cause)

    class IntegrityException(message: String, cause: Throwable? = null) : DatabaseException(message, cause)

    class StorageException(message: String, cause: Throwable? = null) : DatabaseException(message, cause)
}