package com.yfy.basearchitecture.core.ui.impl.dialog

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.managers.DialogConfig
import com.yfy.basearchitecture.core.ui.api.managers.DialogManager
import com.yfy.basearchitecture.core.ui.api.managers.DialogType
import javax.inject.Inject

class DialogManagerImpl @Inject constructor(
    private val context: Context
) : DialogManager {

    private val activeDialogs = mutableListOf<DialogConfig>()
    private var isLoadingDialogShowing = false
    private var currentLoadingMessage = ""
    
    // Callback for dialog state changes
    private var dialogStateCallback: ((List<DialogConfig>, Boolean, String) -> Unit)? = null

    override fun showAlert(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositiveClick: (() -> Unit)?
    ) {
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = title,
            message = message,
            positiveButtonText = positiveButtonText,
            onPositiveClick = onPositiveClick
        )
        showDialog(config)
    }

    override fun showConfirmation(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
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
        showDialog(config)
    }

    override fun showLoading(message: String) {
        isLoadingDialogShowing = true
        currentLoadingMessage = message
        notifyDialogStateChanged()
    }

    override fun hideLoading() {
        isLoadingDialogShowing = false
        currentLoadingMessage = ""
        notifyDialogStateChanged()
    }

    override fun showCustomDialog(config: DialogConfig) {
        showDialog(config)
    }

    override fun showDialog(config: DialogConfig) {
        activeDialogs.add(config)
        notifyDialogStateChanged()
    }

    override fun hideAllDialogs() {
        activeDialogs.clear()
        isLoadingDialogShowing = false
        currentLoadingMessage = ""
        notifyDialogStateChanged()
    }

    override fun isAnyDialogShowing(): Boolean {
        return activeDialogs.isNotEmpty() || isLoadingDialogShowing
    }

    override fun getCurrentDialogCount(): Int {
        return activeDialogs.size + if (isLoadingDialogShowing) 1 else 0
    }

    /**
     * Set callback for dialog state changes
     */
    fun setDialogStateCallback(callback: (List<DialogConfig>, Boolean, String) -> Unit) {
        dialogStateCallback = callback
    }

    /**
     * Get current dialog state
     */
    fun getDialogState(): Triple<List<DialogConfig>, Boolean, String> {
        return Triple(activeDialogs.toList(), isLoadingDialogShowing, currentLoadingMessage)
    }

    /**
     * Remove a specific dialog
     */
    fun removeDialog(config: DialogConfig) {
        activeDialogs.remove(config)
        notifyDialogStateChanged()
    }

    /**
     * Notify dialog state changed
     */
    private fun notifyDialogStateChanged() {
        dialogStateCallback?.invoke(activeDialogs.toList(), isLoadingDialogShowing, currentLoadingMessage)
    }
} 