package com.yfy.basearchitecture.core.ui.api.extensions

import android.content.Context
import java.io.InputStream

/**
 * Extension function to read JSON from raw resources and convert to model
 * @param file Raw resource file ID (e.g., R.raw.auth_responses)
 * @return Parsed model object
 */
inline fun <reified T> Context.getJson(file: Int): T {
    val input: InputStream = resources.openRawResource(file)
    val bytes = ByteArray(input.available())
    input.read(bytes)
    input.close()
    
    val jsonString = String(bytes)
    return jsonString.toModel()
}

/**
 * Extension function to read JSON string and convert to model
 * @param jsonString JSON string to parse
 * @return Parsed model object
 */
inline fun <reified T> Context.getJsonFromString(jsonString: String): T {
    return jsonString.toModel()
}
