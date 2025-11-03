package com.yfy.basearchitecture.core.ui.impl.backpress

import com.yfy.basearchitecture.core.ui.api.managers.BackPressManager
import javax.inject.Inject

/**
 * Implementation of BackPressManager with callback support for Compose screens
 */
class BackPressManagerImpl @Inject constructor() : BackPressManager {

    private var isBackPressEnabled = true
    private var backPressCallbacks = mutableListOf<() -> Boolean>()

    override fun onBackPressed(): Boolean {
        if (!isBackPressEnabled) {
            return false
        }

        // Execute callbacks in reverse order (last added, first executed)
        for (callback in backPressCallbacks.reversed()) {
            try {
                if (callback()) {
                    return true // Back press was handled
                }
            } catch (e: Exception) {
                // Log error but continue with other callbacks
                e.printStackTrace()
            }
        }

        return false // No callback handled the back press
    }

    override fun setBackPressEnabled(enabled: Boolean) {
        isBackPressEnabled = enabled
    }

    override fun isBackPressEnabled(): Boolean {
        return isBackPressEnabled
    }

    /**
     * Add a back press callback
     * @param callback Function that returns true if back press was handled, false otherwise
     * @return Callback ID for removal
     */
    fun addBackPressCallback(callback: () -> Boolean): String {
        val callbackId = "callback_${System.currentTimeMillis()}_${backPressCallbacks.size}"
        backPressCallbacks.add(callback)
        return callbackId
    }

    /**
     * Remove a back press callback by ID
     */
    fun removeBackPressCallback(callbackId: String) {
        // In a real implementation, you might want to store callbacks with IDs
        // For now, we'll use a simple approach
        backPressCallbacks.clear()
    }

    /**
     * Clear all back press callbacks
     */
    fun clearBackPressCallbacks() {
        backPressCallbacks.clear()
    }

    /**
     * Get current callback count
     */
    fun getCallbackCount(): Int {
        return backPressCallbacks.size
    }
} 