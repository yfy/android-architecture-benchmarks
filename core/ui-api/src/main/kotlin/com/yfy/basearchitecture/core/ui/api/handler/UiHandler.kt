package com.yfy.basearchitecture.core.ui.api.handler

import com.yfy.basearchitecture.core.ui.api.managers.BackPressManager
import com.yfy.basearchitecture.core.ui.api.managers.LoaderManager
import com.yfy.basearchitecture.core.ui.api.managers.ToastManager

/**
 * Unified UI handler interface that combines all UI operations
 * This delegates to specific manager interfaces
 */
interface UiHandler : 
    ErrorHandler,
    LoaderManager,
    ToastManager,
    BackPressManager {
    
    // Additional error handling methods
    fun showError(title: String, message: String)
    fun showErrorSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.SHORT)
    fun showErrorDialog(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String? = null,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    )
    fun showRetryDialog(
        message: String,
        onRetry: () -> Unit,
        onCancel: (() -> Unit)? = null
    )
    fun logError(error: BaseError, tag: String = "ErrorHandler")
    
    // Dialog operations that delegate to DialogManager
    fun showAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        onPositiveClick: (() -> Unit)? = null
    )
    
    fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButtonText: String = "Confirm",
        negativeButtonText: String = "Cancel",
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    )
    
    fun showErrorDialogWithRetry(
        error: BaseError,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)? = null,
        onConfirm: (() -> Unit)? = null,
        confirmText: String = "OK",
        retryText: String = "Retry",
        title: String = "Error"
    )
    
    fun hideAllDialogs()
    fun isAnyDialogShowing(): Boolean
}

/**
 * Snackbar duration enum
 */
enum class SnackbarDuration {
    SHORT, LONG, INDEFINITE
} 