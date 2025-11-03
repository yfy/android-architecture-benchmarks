package com.yfy.basearchitecture.core.ui.api.managers

/**
 * Interface for managing back press events
 */
interface BackPressManager {
    
    /**
     * Handle back press event
     * @return true if the back press was handled, false otherwise
     */
    fun onBackPressed(): Boolean
    
    /**
     * Set back press enabled/disabled
     */
    fun setBackPressEnabled(enabled: Boolean)
    
    /**
     * Get back press enabled state
     */
    fun isBackPressEnabled(): Boolean
} 