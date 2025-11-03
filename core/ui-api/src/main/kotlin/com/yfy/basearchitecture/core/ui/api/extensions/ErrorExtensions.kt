package com.yfy.basearchitecture.core.ui.api.extensions

import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.ErrorHandler
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Extension functions for error handling
 */

/**
 * Show error using ErrorHandler
 */
fun BaseError.show(errorHandler: ErrorHandler) {
    errorHandler.showError(this)
}

/**
 * Handle error using ErrorHandler
 */
fun BaseError.handle(errorHandler: ErrorHandler) {
    errorHandler.handleError(this)
}

/**
 * Check if error should be shown to user
 */
fun BaseError.shouldShow(errorHandler: ErrorHandler): Boolean {
    return errorHandler.shouldShowError(this)
}

/**
 * Get user-friendly error message
 */
fun BaseError.getUserMessage(): String {
    return when (this) {
        is BaseError.NetworkError -> message.ifEmpty { "Network error occurred" }
        is BaseError.DatabaseError -> message.ifEmpty { "Database error occurred" }
        is BaseError.ValidationError -> message.ifEmpty { "Validation error occurred" }
        is BaseError.AuthenticationError -> message.ifEmpty { "Authentication error occurred" }
        is BaseError.PermissionError -> message.ifEmpty { "Permission denied" }
        is BaseError.UnknownError -> message.ifEmpty { "An unknown error occurred" }
        is BaseError.ServerError -> message.ifEmpty { "Server error occurred" }
        is BaseError.TimeoutError -> message
        is BaseError.NoInternetError -> message
    }
}

/**
 * Check if error is retryable
 */
fun BaseError.isRetryable(): Boolean {
    return when (this) {
        is BaseError.NetworkError -> retryable
        is BaseError.DatabaseError -> true
        is BaseError.ValidationError -> false
        is BaseError.AuthenticationError -> !shouldLogout
        is BaseError.PermissionError -> false
        is BaseError.UnknownError -> true
        is BaseError.ServerError -> code in 500..599
        is BaseError.TimeoutError -> true
        is BaseError.NoInternetError -> true
    }
}

/**
 * Convert Throwable to BaseError automatically based on exception type
 * Covers common exception types that BaseError doesn't handle directly
 */
fun Throwable.toBaseError(): BaseError {
    return when (this) {
        // Network related errors
        is SocketTimeoutException -> BaseError.TimeoutError(
            message = "Connection timeout. Please check your internet connection and try again.",
            timeout = null
        )
        is UnknownHostException -> BaseError.NoInternetError(
            message = "No internet connection. Please check your network settings."
        )
        is IOException -> BaseError.NetworkError(
            message = message ?: "Network error occurred",
            retryable = true
        )
        
        // Default case for any other exception
        else -> BaseError.UnknownError(
            message = message ?: "An unexpected error occurred",
            throwable = this
        )
    }
}

/**
 * Handle error using ErrorHandler with automatic conversion from Throwable
 */
fun Throwable.handleError(errorHandler: ErrorHandler) {
    errorHandler.handleError(this.toBaseError())
}

/**
 * Show error using ErrorHandler with automatic conversion from Throwable
 */
fun Throwable.showError(errorHandler: ErrorHandler) {
    errorHandler.showError(this.toBaseError())
}

/**
 * Get error code for analytics
 */
fun BaseError.getErrorCode(): String {
    return when (this) {
        is BaseError.NetworkError -> "NETWORK_ERROR"
        is BaseError.DatabaseError -> "DATABASE_ERROR"
        is BaseError.ValidationError -> "VALIDATION_ERROR"
        is BaseError.AuthenticationError -> "AUTH_ERROR"
        is BaseError.PermissionError -> "PERMISSION_ERROR"
        is BaseError.UnknownError -> "UNKNOWN_ERROR"
        is BaseError.ServerError -> "SERVER_ERROR"
        is BaseError.TimeoutError -> "TIMEOUT_ERROR"
        is BaseError.NoInternetError -> "NO_INTERNET_ERROR"
    }
} 