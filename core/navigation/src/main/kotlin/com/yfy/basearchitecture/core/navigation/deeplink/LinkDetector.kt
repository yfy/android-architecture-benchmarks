package com.yfy.basearchitecture.core.navigation.deeplink

object LinkDetector {

    fun detectLinkType(link: String): LinkType {
        return when {
            link.startsWith(NavigationConfig.DEEP_LINK_PREFIX) -> LinkType.DEEP_LINK
            link.startsWith(NavigationConfig.APP_LINK_PREFIX) -> LinkType.APP_LINK
            !link.contains("://") && link.isNotEmpty()-> LinkType.ROUTE
            else -> LinkType.INVALID
        }
    }

    fun isValidLink(link: String): Boolean {
        return detectLinkType(link) != LinkType.INVALID
    }

    fun convertAppLinkToDeepLink(appLink: String): String {
        return if (appLink.startsWith(NavigationConfig.APP_LINK_PREFIX)) {
            appLink.replace(NavigationConfig.APP_LINK_PREFIX, NavigationConfig.DEEP_LINK_PREFIX)
        } else appLink
    }

    fun convertDeepLinkToAppLink(deepLink: String): String {
        return if (deepLink.startsWith(NavigationConfig.DEEP_LINK_PREFIX)) {
            deepLink.replace(NavigationConfig.DEEP_LINK_PREFIX, NavigationConfig.APP_LINK_PREFIX)
        } else deepLink
    }
}