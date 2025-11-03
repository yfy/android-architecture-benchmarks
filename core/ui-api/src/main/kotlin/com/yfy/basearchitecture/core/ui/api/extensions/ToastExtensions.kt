package com.yfy.basearchitecture.core.ui.api.extensions

import com.yfy.basearchitecture.core.ui.api.managers.ToastDuration
import com.yfy.basearchitecture.core.ui.api.managers.ToastManager

/**
 * Extension functions for toast messages
 */

/**
 * Show short toast message
 */
fun String.showShort(toastManager: ToastManager) {
    toastManager.showShort(this)
}

/**
 * Show long toast message
 */
fun String.showLong(toastManager: ToastManager) {
    toastManager.showLong(this)
}

/**
 * Show toast message with custom duration
 */
fun String.showToast(toastManager: ToastManager, duration: ToastDuration = ToastDuration.SHORT) {
    toastManager.show(this, duration)
}

/**
 * Show success toast message
 */
fun String.showSuccess(toastManager: ToastManager, duration: ToastDuration = ToastDuration.SHORT) {
    toastManager.showSuccess(this, duration)
}

/**
 * Show error toast message
 */
fun String.showError(toastManager: ToastManager, duration: ToastDuration = ToastDuration.LONG) {
    toastManager.showError(this, duration)
}

/**
 * Show warning toast message
 */
fun String.showWarning(toastManager: ToastManager, duration: ToastDuration = ToastDuration.SHORT) {
    toastManager.showWarning(this, duration)
}

/**
 * Show info toast message
 */
fun String.showInfo(toastManager: ToastManager, duration: ToastDuration = ToastDuration.SHORT) {
    toastManager.showInfo(this, duration)
}

/**
 * Extension for nullable strings
 */
fun String?.showShortOrEmpty(toastManager: ToastManager, defaultMessage: String = "") {
    toastManager.showShort(this ?: defaultMessage)
}

fun String?.showLongOrEmpty(toastManager: ToastManager, defaultMessage: String = "") {
    toastManager.showLong(this ?: defaultMessage)
}

fun String?.showSuccessOrEmpty(toastManager: ToastManager, defaultMessage: String = "") {
    toastManager.showSuccess(this ?: defaultMessage)
}

fun String?.showErrorOrEmpty(toastManager: ToastManager, defaultMessage: String = "") {
    toastManager.showError(this ?: defaultMessage)
} 