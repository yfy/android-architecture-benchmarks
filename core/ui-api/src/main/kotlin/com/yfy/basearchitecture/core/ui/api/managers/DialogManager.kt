package com.yfy.basearchitecture.core.ui.api.managers

/**
 * Interface for managing dialogs in the application
 */
interface DialogManager {
    
    /**
     * Show alert dialog
     */
    fun showAlert(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        onPositiveClick: (() -> Unit)? = null
    )
    
    /**
     * Show confirmation dialog
     */
    fun showConfirmation(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String = "Cancel",
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    )
    
    /**
     * Show loading dialog
     */
    fun showLoading(message: String = "Loading...")
    
    /**
     * Hide loading dialog
     */
    fun hideLoading()
    
    /**
     * Show custom dialog
     */
    fun showCustomDialog(config: DialogConfig)
    
    /**
     * Show dialog with configuration
     */
    fun showDialog(config: DialogConfig)
    
    /**
     * Hide all dialogs
     */
    fun hideAllDialogs()
    
    /**
     * Check if any dialog is showing
     */
    fun isAnyDialogShowing(): Boolean
    
    /**
     * Get current dialog count
     */
    fun getCurrentDialogCount(): Int
}

/**
 * Dialog type enum
 */
enum class DialogType {
    ALERT,
    CONFIRMATION,
    INPUT,
    LOADING,
    CUSTOM
}

/**
 * Dialog configuration data class
 */
data class DialogConfig(
    val type: DialogType = DialogType.ALERT,
    val title: String? = null,
    val message: String? = null,
    val positiveButtonText: String? = null,
    val negativeButtonText: String? = null,
    val neutralButtonText: String? = null,
    val dismissible: Boolean = true,
    val cancelable: Boolean = true,
    val onPositiveClick: (() -> Unit)? = null,
    val onNegativeClick: (() -> Unit)? = null,
    val onNeutralClick: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
) 