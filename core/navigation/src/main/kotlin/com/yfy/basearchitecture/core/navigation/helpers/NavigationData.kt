package com.yfy.basearchitecture.core.navigation.helpers

import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class NavigationData(
    val dataKey: String,
    val extras: Map<String, String> = emptyMap()
)
