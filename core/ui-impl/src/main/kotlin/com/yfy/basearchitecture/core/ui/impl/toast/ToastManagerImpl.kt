package com.yfy.basearchitecture.core.ui.impl.toast

import android.content.Context
import android.widget.Toast
import com.yfy.basearchitecture.core.ui.api.managers.ToastDuration
import com.yfy.basearchitecture.core.ui.api.managers.ToastManager
import javax.inject.Inject

class ToastManagerImpl @Inject constructor(
    private val context: Context
) : ToastManager {

    private var currentToast: Toast? = null

    override fun showShort(message: String) {
        show(message, ToastDuration.SHORT)
    }

    override fun showLong(message: String) {
        show(message, ToastDuration.LONG)
    }

    override fun show(message: String, duration: ToastDuration) {
        cancelAll() // Cancel previous toast
        val toastDuration = when (duration) {
            ToastDuration.SHORT -> Toast.LENGTH_SHORT
            ToastDuration.LONG -> Toast.LENGTH_LONG
        }
        currentToast = Toast.makeText(context, message, toastDuration)
        currentToast?.show()
    }

    override fun showSuccess(message: String, duration: ToastDuration) {
        show("✅ $message", duration)
    }

    override fun showError(message: String, duration: ToastDuration) {
        show("❌ $message", duration)
    }

    override fun showWarning(message: String, duration: ToastDuration) {
        show("⚠️ $message", duration)
    }

    override fun showInfo(message: String, duration: ToastDuration) {
        show("ℹ️ $message", duration)
    }

    override fun cancelAll() {
        currentToast?.cancel()
        currentToast = null
    }

    override fun isShowing(): Boolean {
        return currentToast != null
    }
} 