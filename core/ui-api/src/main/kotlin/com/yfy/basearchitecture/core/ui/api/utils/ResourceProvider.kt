package com.yfy.basearchitecture.core.ui.api.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Interface for providing application resources
 */
interface ResourceProvider {
    
    /**
     * Get string resource
     */
    fun getString(resId: Int, vararg formatArgs: Any): String
    
    /**
     * Get string resource by key
     */
    fun getString(key: String, vararg formatArgs: Any): String
    
    /**
     * Get color resource
     */
    fun getColor(resId: Int): Color
    
    /**
     * Get dimension resource
     */
    fun getDimension(resId: Int): Dp
    
    /**
     * Get drawable resource name
     */
    fun getDrawableName(resId: Int): String
    
    /**
     * Get boolean resource
     */
    fun getBoolean(resId: Int): Boolean
    
    /**
     * Get integer resource
     */
    fun getInteger(resId: Int): Int
    
    /**
     * Get string array resource
     */
    fun getStringArray(resId: Int): Array<String>
    
    /**
     * Get integer array resource
     */
    fun getIntegerArray(resId: Int): IntArray
    
    /**
     * Check if resource exists
     */
    fun hasResource(resId: Int): Boolean
    
    /**
     * Get resource identifier by name
     */
    fun getResourceId(resourceName: String, resourceType: String): Int
    
    /**
     * Get resource name by identifier
     */
    fun getResourceName(resId: Int): String?
} 