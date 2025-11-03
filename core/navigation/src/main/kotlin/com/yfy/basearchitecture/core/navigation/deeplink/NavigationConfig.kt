package com.yfy.basearchitecture.core.navigation.deeplink

object NavigationConfig {
    const val DEEP_LINK_SCHEME = "yfy"
    const val APP_LINK_DOMAIN = "example.com"
    const val DEEP_LINK_PREFIX = "$DEEP_LINK_SCHEME://"
    const val APP_LINK_PREFIX = "https://$APP_LINK_DOMAIN/"
}

enum class LinkType {
    DEEP_LINK,
    APP_LINK,
    ROUTE,
    INVALID
}