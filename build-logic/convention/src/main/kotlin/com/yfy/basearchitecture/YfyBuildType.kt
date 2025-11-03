package com.yfy.basearchitecture

enum class YfyBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}