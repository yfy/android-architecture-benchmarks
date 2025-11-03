package com.yfy.basearchitecture.core.ui.api.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider

/**
 * Extension functions for resource access
 */

/**
 * Get string resource by ID
 */
fun Int.getString(resourceProvider: ResourceProvider, vararg formatArgs: Any): String {
    return resourceProvider.getString(this, *formatArgs)
}

/**
 * Get string resource by key
 */
fun String.getString(resourceProvider: ResourceProvider, vararg formatArgs: Any): String {
    return resourceProvider.getString(this, *formatArgs)
}

/**
 * Get color resource by ID
 */
fun Int.getColor(resourceProvider: ResourceProvider): Color {
    return resourceProvider.getColor(this)
}

/**
 * Get dimension resource by ID
 */
fun Int.getDimension(resourceProvider: ResourceProvider): Dp {
    return resourceProvider.getDimension(this)
}

/**
 * Get boolean resource by ID
 */
fun Int.getBoolean(resourceProvider: ResourceProvider): Boolean {
    return resourceProvider.getBoolean(this)
}

/**
 * Get integer resource by ID
 */
fun Int.getInteger(resourceProvider: ResourceProvider): Int {
    return resourceProvider.getInteger(this)
}

/**
 * Get string array resource by ID
 */
fun Int.getStringArray(resourceProvider: ResourceProvider): Array<String> {
    return resourceProvider.getStringArray(this)
}

/**
 * Get integer array resource by ID
 */
fun Int.getIntegerArray(resourceProvider: ResourceProvider): IntArray {
    return resourceProvider.getIntegerArray(this)
}

/**
 * Check if resource exists
 */
fun Int.hasResource(resourceProvider: ResourceProvider): Boolean {
    return resourceProvider.hasResource(this)
}

/**
 * Get resource name by ID
 */
fun Int.getResourceName(resourceProvider: ResourceProvider): String? {
    return resourceProvider.getResourceName(this)
}

/**
 * Get drawable name by ID
 */
fun Int.getDrawableName(resourceProvider: ResourceProvider): String {
    return resourceProvider.getDrawableName(this)
} 