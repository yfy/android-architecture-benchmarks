package com.yfy.basearchitecture.core.ui.impl.resources

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    private val context: Context
) : ResourceProvider {

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return try {
            if (formatArgs.isEmpty()) {
                context.getString(resId)
            } else {
                context.getString(resId, *formatArgs)
            }
        } catch (e: Exception) {
            "String not found: $resId"
        }
    }

    override fun getString(key: String, vararg formatArgs: Any): String {
        return try {
            val resId = context.resources.getIdentifier(key, "string", context.packageName)
            if (resId != 0) {
                getString(resId, *formatArgs)
            } else {
                key
            }
        } catch (e: Exception) {
            key
        }
    }

    override fun getColor(resId: Int): Color {
        return try {
            val colorInt = ContextCompat.getColor(context, resId)
            Color(colorInt)
        } catch (e: Exception) {
            Color.Black
        }
    }

    override fun getDimension(resId: Int): Dp {
        return try {
            val dimension = context.resources.getDimension(resId)
            dimension.dp
        } catch (e: Exception) {
            0.dp
        }
    }

    override fun getDrawableName(resId: Int): String {
        return try {
            context.resources.getResourceEntryName(resId)
        } catch (e: Exception) {
            "drawable_not_found"
        }
    }

    override fun getBoolean(resId: Int): Boolean {
        return try {
            context.resources.getBoolean(resId)
        } catch (e: Exception) {
            false
        }
    }

    override fun getInteger(resId: Int): Int {
        return try {
            context.resources.getInteger(resId)
        } catch (e: Exception) {
            0
        }
    }

    override fun getStringArray(resId: Int): Array<String> {
        return try {
            context.resources.getStringArray(resId)
        } catch (e: Exception) {
            emptyArray()
        }
    }

    override fun getIntegerArray(resId: Int): IntArray {
        return try {
            context.resources.getIntArray(resId)
        } catch (e: Exception) {
            intArrayOf()
        }
    }

    override fun hasResource(resId: Int): Boolean {
        return try {
            context.resources.getResourceName(resId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getResourceId(resourceName: String, resourceType: String): Int {
        return try {
            context.resources.getIdentifier(resourceName, resourceType, context.packageName)
        } catch (e: Exception) {
            0
        }
    }

    override fun getResourceName(resId: Int): String? {
        return try {
            context.resources.getResourceName(resId)
        } catch (e: Exception) {
            null
        }
    }
} 