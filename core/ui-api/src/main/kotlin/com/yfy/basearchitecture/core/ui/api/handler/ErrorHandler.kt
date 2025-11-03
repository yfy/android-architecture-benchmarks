package com.yfy.basearchitecture.core.ui.api.handler

/**
 * Interface for handling errors in the application
 */
interface ErrorHandler {
    
    /**
     * Handle error
     */
    fun handleError(error: BaseError)
    
    /**
     * Show error to user
     */
    fun showError(error: BaseError)
    
    /**
     * Check if error should be shown to user
     */
    fun shouldShowError(error: BaseError): Boolean
}