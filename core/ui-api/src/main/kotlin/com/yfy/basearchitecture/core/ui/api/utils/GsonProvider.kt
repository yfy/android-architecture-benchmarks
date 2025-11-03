package com.yfy.basearchitecture.core.ui.api.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Singleton object providing a single Gson instance for the entire app
 */
object GsonProvider {
    
    val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }
}
