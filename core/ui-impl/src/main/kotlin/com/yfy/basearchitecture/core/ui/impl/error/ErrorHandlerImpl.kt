package com.yfy.basearchitecture.core.ui.impl.error

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.ErrorHandler
import com.yfy.basearchitecture.core.ui.api.handler.SnackbarDuration
import timber.log.Timber
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor(
    private val context: Context
) : ErrorHandler {

    // Callback for error state changes
    private var errorStateCallback: ((BaseError?, String, SnackbarDuration) -> Unit)? = null
    private var errorDialogCallback: ((BaseError, String, String, String?, (() -> Unit)?, (() -> Unit)?) -> Unit)? = null

    override fun handleError(error: BaseError) {
        logError(error)
        showError(error)
    }

    override fun showError(error: BaseError) {
        val message = error.getUserMessage()
        showErrorSnackbar(message, SnackbarDuration.LONG)
    }

    override fun shouldShowError(error: BaseError): Boolean {
        return when (error) {
            is BaseError.ValidationError -> true
            is BaseError.NetworkError -> true
            is BaseError.AuthenticationError -> true
            is BaseError.PermissionError -> true
            is BaseError.ServerError -> true
            is BaseError.TimeoutError -> true
            is BaseError.NoInternetError -> true
            is BaseError.DatabaseError -> false // Don't show database errors to user
            is BaseError.UnknownError -> false // Don't show unknown errors to user
        }
    }

    /**
     * Show error with title and message
     */
    fun showError(title: String, message: String) {
        // Create a simple error and show it
        val error = BaseError.UnknownError("$title: $message")
        showError(error)
    }

    /**
     * Show error snackbar
     */
    fun showErrorSnackbar(message: String, duration: SnackbarDuration) {
        // Notify through callback for real implementation
        errorStateCallback?.invoke(null, message, duration)
    }

    /**
     * Show error dialog
     */
    fun showErrorDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String?,
        onPositiveClick: (() -> Unit)?,
        onNegativeClick: (() -> Unit)?
    ) {
        // Create a simple error for dialog
        val error = BaseError.UnknownError(message)
        errorDialogCallback?.invoke(error, title, positiveButtonText, negativeButtonText, onPositiveClick, onNegativeClick)
    }

    /**
     * Show retry dialog
     */
    fun showRetryDialog(
        message: String,
        onRetry: () -> Unit,
        onCancel: (() -> Unit)?
    ) {
        // Create a retry error
        val error = BaseError.NetworkError(message, retryable = true)
        errorDialogCallback?.invoke(error, "Retry", "Retry", "Cancel", onRetry, onCancel)
    }

    /**
     * Log error
     */
    fun logError(error: BaseError, tag: String = "ErrorHandler") {
        Timber.e(tag, "Error occurred: ${error.getUserMessage()}")
        Timber.e(tag, "Error type: ${error.getErrorCode()}")
        Timber.e(tag, "Error retryable: ${error.isRetryable()}")
    }

    /**
     * Show error dialog with BaseError parameter
     * This method is used by UiHandler.showErrorDialogWithRetry
     */
    fun showErrorDialog(
        error: BaseError,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)? = null,
        onConfirm: (() -> Unit)? = null,
        confirmText: String = "OK",
        retryText: String = "Retry",
        title: String = "Error"
    ) {
        val finalTitle = title.ifEmpty { "Error" }
        val finalConfirmText = if (error.isRetryable() && onRetry != null) retryText else confirmText
        val finalNegativeText = if (error.isRetryable() && onRetry != null) "Cancel" else null
        
        val onPositiveClick = if (error.isRetryable() && onRetry != null) {
            { onRetry(); onDismiss() }
        } else {
            { onConfirm?.invoke(); onDismiss() }
        }
        
        val onNegativeClick = if (error.isRetryable() && onRetry != null) {
            { onDismiss() }
        } else null
        
        errorDialogCallback?.invoke(error, finalTitle, finalConfirmText, finalNegativeText, onPositiveClick, onNegativeClick)
    }

    /**
     * Set callback for error snackbar
     */
    fun setErrorSnackbarCallback(callback: (BaseError?, String, SnackbarDuration) -> Unit) {
        errorStateCallback = callback
    }

    /**
     * Set callback for error dialog
     */
    fun setErrorDialogCallback(callback: (BaseError, String, String, String?, (() -> Unit)?, (() -> Unit)?) -> Unit) {
        errorDialogCallback = callback
    }
} 