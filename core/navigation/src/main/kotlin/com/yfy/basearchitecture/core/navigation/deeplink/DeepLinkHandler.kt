package com.yfy.basearchitecture.core.navigation.deeplink

import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import timber.log.Timber

object DeepLinkHandler {

    fun normalizeToDeepLink(link: String): String? {
        if (link.isBlank()) return null
        return when (LinkDetector.detectLinkType(link)) {
            LinkType.DEEP_LINK -> link
            LinkType.APP_LINK -> LinkDetector.convertAppLinkToDeepLink(link)
            else -> null
        }
    }

    fun buildDeepLinkRequest(link: String): NavDeepLinkRequest? {
        val deepLink = normalizeToDeepLink(link) ?: return null
        return try {
            val uri = deepLink.toUri()
            NavDeepLinkRequest.Builder.fromUri(uri).build()
        } catch (e: Exception) {
            Timber.e("DeepLinkHandler", "Failed to build deep link request from: $link", e)
            null
        }
    }

    fun extractParameters(deepLink: String): Map<String, String> {
        return try {
            val uri = deepLink.toUri()
            val params = mutableMapOf<String, String>()

            // Query parameters
            uri.queryParameterNames.forEach { paramName ->
                uri.getQueryParameter(paramName)?.let { value ->
                    params[paramName] = value
                }
            }

            // Path parameters (capture numeric segments as id by convention)
            val pathSegments = uri.pathSegments
            if (pathSegments.isNotEmpty()) {
                pathSegments.forEach { segment ->
                    if (segment.matches(Regex("\\d+"))) {
                        params["id"] = segment
                    }
                }
            }

            params
        } catch (e: Exception) {
            Timber.e("DeepLinkHandler", "Failed to extract parameters from: $deepLink", e)
            emptyMap()
        }
    }
}