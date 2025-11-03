package com.yfy.basearchitecture.core.ui.api.managers

/**
 * Interface for managing toast messages in the application
 */
interface ToastManager {
    
    /**
     * Show short toast message
     */
    fun showShort(message: String)
    
    /**
     * Show long toast message
     */
    fun showLong(message: String)
    
    /**
     * Show toast message with custom duration
     */
    fun show(message: String, duration: ToastDuration = ToastDuration.SHORT)
    
    /**
     * Show success toast message
     */
    fun showSuccess(message: String, duration: ToastDuration = ToastDuration.SHORT)
    
    /**
     * Show error toast message
     */
    fun showError(message: String, duration: ToastDuration = ToastDuration.LONG)
    
    /**
     * Show warning toast message
     */
    fun showWarning(message: String, duration: ToastDuration = ToastDuration.SHORT)
    
    /**
     * Show info toast message
     */
    fun showInfo(message: String, duration: ToastDuration = ToastDuration.SHORT)
    
    /**
     * Cancel all toast messages
     */
    fun cancelAll()
    
    /**
     * Check if toast is currently showing
     */
    fun isShowing(): Boolean
}

/**
 * Toast duration enum
 */
enum class ToastDuration {
    SHORT, LONG
} 