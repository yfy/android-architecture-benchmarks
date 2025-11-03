package com.yfy.basearchitecture.core.ui.impl.loader

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.managers.LoaderConfig
import com.yfy.basearchitecture.core.ui.api.managers.LoaderManager
import com.yfy.basearchitecture.core.ui.api.managers.LoaderType
import javax.inject.Inject

class LoaderManagerImpl @Inject constructor(
    private val context: Context
) : LoaderManager {

    private var currentLoaderConfig: LoaderConfig? = null
    private var isLoaderShowing = false
    
    // Callback for loader state changes
    private var loaderStateCallback: ((LoaderConfig?, Boolean) -> Unit)? = null

    override fun showLoader(message: String) {
        showLoader(LoaderType.CIRCULAR, message)
    }

    override fun hideLoader() {
        isLoaderShowing = false
        currentLoaderConfig = null
        notifyLoaderStateChanged()
    }

    override fun showLoader(type: LoaderType, message: String) {
        val config = LoaderConfig(
            type = type,
            message = message
        )
        currentLoaderConfig = config
        isLoaderShowing = true
        notifyLoaderStateChanged()
    }

    override fun isLoaderShowing(): Boolean {
        return isLoaderShowing
    }

    override fun getCurrentLoaderMessage(): String? {
        return currentLoaderConfig?.message
    }

    override fun getCurrentLoaderType(): LoaderType? {
        return currentLoaderConfig?.type
    }

    /**
     * Set callback for loader state changes
     */
    fun setLoaderStateCallback(callback: (LoaderConfig?, Boolean) -> Unit) {
        loaderStateCallback = callback
    }

    /**
     * Get current loader state
     */
    fun getLoaderState(): Pair<LoaderConfig?, Boolean> {
        return currentLoaderConfig to isLoaderShowing
    }

    /**
     * Notify loader state changed
     */
    private fun notifyLoaderStateChanged() {
        loaderStateCallback?.invoke(currentLoaderConfig, isLoaderShowing)
    }
} 