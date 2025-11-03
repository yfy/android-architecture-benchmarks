package com.yfy.basearchitecture.core.ui.impl.handler

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.ErrorHandler
import com.yfy.basearchitecture.core.ui.api.handler.SnackbarDuration
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import com.yfy.basearchitecture.core.ui.api.managers.BackPressManager
import com.yfy.basearchitecture.core.ui.api.managers.DialogConfig
import com.yfy.basearchitecture.core.ui.api.managers.DialogManager
import com.yfy.basearchitecture.core.ui.api.managers.DialogType
import com.yfy.basearchitecture.core.ui.api.managers.LoaderManager
import com.yfy.basearchitecture.core.ui.api.managers.LoaderType
import com.yfy.basearchitecture.core.ui.api.managers.ToastDuration
import com.yfy.basearchitecture.core.ui.api.managers.ToastManager
import com.yfy.basearchitecture.core.ui.impl.backpress.BackPressManagerImpl
import com.yfy.basearchitecture.core.ui.impl.dialog.DialogManagerImpl
import com.yfy.basearchitecture.core.ui.impl.error.ErrorHandlerImpl
import com.yfy.basearchitecture.core.ui.impl.loader.LoaderManagerImpl
import com.yfy.basearchitecture.core.ui.impl.toast.ToastManagerImpl
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of UiHandler that delegates to specific managers
 */
class UiHandlerImpl @Inject constructor(
    context: Context
) : UiHandler {

    private val errorHandler: ErrorHandler = ErrorHandlerImpl(context)
    private val loaderManager: LoaderManager = LoaderManagerImpl(context)
    private val toastManager: ToastManager = ToastManagerImpl(context)
    private val dialogManager: DialogManager = DialogManagerImpl(context)
    private val backPressManager: BackPressManager = BackPressManagerImpl()

    // ErrorHandler delegation
    override fun handleError(error: BaseError) {
        errorHandler.handleError(error)
    }

    override fun showError(error: BaseError) {
        errorHandler.showError(error)
    }

    override fun shouldShowError(error: BaseError): Boolean {
        return errorHandler.shouldShowError(error)
    }

    // Additional error handling methods
    override fun showError(title: String, message: String) {
        // Create a simple error and show it
        val error = BaseError.UnknownError("$title: $message")
        showError(error)
    }

    override fun showErrorSnackbar(message: String, duration: SnackbarDuration) {
        val toastDuration = when (duration) {
            SnackbarDuration.SHORT -> ToastDuration.SHORT
            SnackbarDuration.LONG -> ToastDuration.LONG
            SnackbarDuration.INDEFINITE -> ToastDuration.LONG
        }
        toastManager.showError(message, toastDuration)
    }

    override fun showErrorDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String?,
        onPositiveClick: (() -> Unit)?,
        onNegativeClick: (() -> Unit)?
    ) {
        val config = DialogConfig(
            type = DialogType.CONFIRMATION,
            title = title,
            message = message,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            onPositiveClick = onPositiveClick,
            onNegativeClick = onNegativeClick
        )
        dialogManager.showDialog(config)
    }

    override fun showRetryDialog(
        message: String,
        onRetry: () -> Unit,
        onCancel: (() -> Unit)?
    ) {
        val config = DialogConfig(
            type = DialogType.CONFIRMATION,
            title = "Retry",
            message = message,
            positiveButtonText = "Retry",
            negativeButtonText = "Cancel",
            onPositiveClick = onRetry,
            onNegativeClick = onCancel
        )
        dialogManager.showDialog(config)
    }

    override fun logError(error: BaseError, tag: String) {
        Timber.e(tag, "Error: ${error.getUserMessage()}")
    }

    // LoaderManager delegation
    override fun showLoader(message: String) {
        loaderManager.showLoader(message)
    }

    override fun hideLoader() {
        loaderManager.hideLoader()
    }

    override fun showLoader(type: LoaderType, message: String) {
        loaderManager.showLoader(type, message)
    }

    override fun isLoaderShowing(): Boolean {
        return loaderManager.isLoaderShowing()
    }

    override fun getCurrentLoaderMessage(): String? {
        return loaderManager.getCurrentLoaderMessage()
    }

    override fun getCurrentLoaderType(): LoaderType? {
        return loaderManager.getCurrentLoaderType()
    }

    // ToastManager delegation
    override fun showShort(message: String) {
        toastManager.showShort(message)
    }

    override fun showLong(message: String) {
        toastManager.showLong(message)
    }

    override fun show(message: String, duration: ToastDuration) {
        toastManager.show(message, duration)
    }

    override fun showSuccess(message: String, duration: ToastDuration) {
        toastManager.showSuccess(message, duration)
    }

    override fun showError(message: String, duration: ToastDuration) {
        toastManager.showError(message, duration)
    }

    override fun showWarning(message: String, duration: ToastDuration) {
        toastManager.showWarning(message, duration)
    }

    override fun showInfo(message: String, duration: ToastDuration) {
        toastManager.showInfo(message, duration)
    }

    override fun cancelAll() {
        toastManager.cancelAll()
    }

    override fun isShowing(): Boolean {
        return toastManager.isShowing()
    }

    // BackPressManager delegation
    override fun onBackPressed(): Boolean {
        return backPressManager.onBackPressed()
    }

    override fun setBackPressEnabled(enabled: Boolean) {
        backPressManager.setBackPressEnabled(enabled)
    }

    override fun isBackPressEnabled(): Boolean {
        return backPressManager.isBackPressEnabled()
    }

    // Dialog operations that delegate to DialogManager
    override fun showAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositiveClick: (() -> Unit)?
    ) {
        dialogManager.showAlert(title, message, positiveButtonText, onPositiveClick)
    }

    override fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        onPositiveClick: (() -> Unit)?,
        onNegativeClick: (() -> Unit)?
    ) {
        dialogManager.showConfirmation(
            title, message, positiveButtonText, negativeButtonText, 
            onPositiveClick, onNegativeClick
        )
    }

    override fun showErrorDialogWithRetry(
        error: BaseError,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
        onConfirm: (() -> Unit)?,
        confirmText: String,
        retryText: String,
        title: String
    ) {
        // Create a custom dialog config for error dialog
        val config = DialogConfig(
            type = DialogType.CONFIRMATION,
            title = title,
            message = error.getUserMessage(),
            positiveButtonText = if (error.isRetryable() && onRetry != null) retryText else confirmText,
            negativeButtonText = if (error.isRetryable() && onRetry != null) "Cancel" else null,
            onPositiveClick = if (error.isRetryable() && onRetry != null) {
                { onRetry(); onDismiss() }
            } else {
                { onConfirm?.invoke(); onDismiss() }
            },
            onNegativeClick = if (error.isRetryable() && onRetry != null) {
                { onDismiss() }
            } else null
        )
        dialogManager.showDialog(config)
    }

    /**
     * Show error dialog with BaseError parameter
     * This method is used by BaseActivity.showErrorDialog
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
        showErrorDialogWithRetry(error, onDismiss, onRetry, onConfirm, confirmText, retryText, title)
    }

    override fun hideAllDialogs() {
        dialogManager.hideAllDialogs()
    }

    override fun isAnyDialogShowing(): Boolean {
        return dialogManager.isAnyDialogShowing()
    }
} 