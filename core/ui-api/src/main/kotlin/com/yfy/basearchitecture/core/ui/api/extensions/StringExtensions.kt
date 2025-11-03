package com.yfy.basearchitecture.core.ui.api.extensions

import com.google.gson.reflect.TypeToken
import com.yfy.basearchitecture.core.ui.api.utils.GsonProvider

/**
 * Extension function to convert JSON string to model
 * @return Parsed model object
 */
inline fun <reified T> String.toModel(): T {
    return GsonProvider.gson.fromJson(this, object : TypeToken<T>() {}.type)
}
