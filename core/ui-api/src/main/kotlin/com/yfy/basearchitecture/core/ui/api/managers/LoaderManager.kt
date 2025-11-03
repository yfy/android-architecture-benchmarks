package com.yfy.basearchitecture.core.ui.api.managers

/**
 * Interface for managing loading states in the application
 */
interface LoaderManager {
    
    /**
     * Show loader
     */
    fun showLoader(message: String = "Loading...")
    
    /**
     * Hide loader
     */
    fun hideLoader()
    
    /**
     * Show loader with custom type
     */
    fun showLoader(type: LoaderType, message: String = "Loading...")
    
    /**
     * Check if loader is showing
     */
    fun isLoaderShowing(): Boolean
    
    /**
     * Get current loader message
     */
    fun getCurrentLoaderMessage(): String?
    
    /**
     * Get current loader type
     */
    fun getCurrentLoaderType(): LoaderType?
}

/**
 * Loader type enum
 */
enum class LoaderType {
    CIRCULAR,
    LINEAR,
    DOTS,
    PULSE,
    CUSTOM
}

/**
 * Loader configuration data class
 */
data class LoaderConfig(
    val type: LoaderType = LoaderType.CIRCULAR,
    val message: String = "Loading...",
    val isCancelable: Boolean = false,
    val onCancel: (() -> Unit)? = null
) 