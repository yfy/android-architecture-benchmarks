package com.yfy.basearchitecture.core.ui.api.handler

/**
 * Base error class for handling different types of errors in the application
 */
sealed class BaseError {
    data class NetworkError(
        val message: String,
        val code: Int? = null,
        val retryable: Boolean = true
    ) : BaseError()

    data class DatabaseError(
        val message: String,
        val table: String? = null
    ) : BaseError()

    data class ValidationError(
        val field: String,
        val message: String,
        val value: Any? = null
    ) : BaseError()

    data class AuthenticationError(
        val message: String,
        val shouldLogout: Boolean = false
    ) : BaseError()

    data class PermissionError(
        val message: String,
        val permission: String? = null
    ) : BaseError()

    data class UnknownError(
        val message: String,
        val throwable: Throwable? = null
    ) : BaseError()

    data class ServerError(
        val message: String,
        val code: Int,
        val details: Map<String, Any> = emptyMap()
    ) : BaseError()

    data class TimeoutError(
        val message: String = "Request timed out",
        val timeout: Long? = null
    ) : BaseError()

    data class NoInternetError(
        val message: String = "No internet connection"
    ) : BaseError()

    /**
     * Get user-friendly error message
     */
    fun getUserMessage(): String {
        return when (this) {
            is NetworkError -> message.ifEmpty { "Network error occurred" }
            is DatabaseError -> message.ifEmpty { "Database error occurred" }
            is ValidationError -> message.ifEmpty { "Validation error occurred" }
            is AuthenticationError -> message.ifEmpty { "Authentication error occurred" }
            is PermissionError -> message.ifEmpty { "Permission denied" }
            is UnknownError -> message.ifEmpty { "An unknown error occurred" }
            is ServerError -> message.ifEmpty { "Server error occurred" }
            is TimeoutError -> message
            is NoInternetError -> message
        }
    }

    /**
     * Check if error is retryable
     */
    fun isRetryable(): Boolean {
        return when (this) {
            is NetworkError -> retryable
            is DatabaseError -> true
            is ValidationError -> false
            is AuthenticationError -> !shouldLogout
            is PermissionError -> false
            is UnknownError -> true
            is ServerError -> code in 500..599
            is TimeoutError -> true
            is NoInternetError -> true
        }
    }

    /**
     * Get error code for analytics
     */
    fun getErrorCode(): String {
        return when (this) {
            is NetworkError -> "NETWORK_ERROR"
            is DatabaseError -> "DATABASE_ERROR"
            is ValidationError -> "VALIDATION_ERROR"
            is AuthenticationError -> "AUTH_ERROR"
            is PermissionError -> "PERMISSION_ERROR"
            is UnknownError -> "UNKNOWN_ERROR"
            is ServerError -> "SERVER_ERROR"
            is TimeoutError -> "TIMEOUT_ERROR"
            is NoInternetError -> "NO_INTERNET_ERROR"
        }
    }
} 