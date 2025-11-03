package com.yfy.basearchitecture.core.navigation.constants

import com.yfy.basearchitecture.core.navigation.deeplink.NavigationConfig

object DeepLinks {

    object Auth {
        const val LOGIN = "${NavigationConfig.DEEP_LINK_PREFIX}auth/login"
        const val REGISTER = "${NavigationConfig.DEEP_LINK_PREFIX}auth/register"
        const val FORGOT_PASSWORD = "${NavigationConfig.DEEP_LINK_PREFIX}auth/forgot-password"

        fun loginWithEmail(email: String) = "$LOGIN?email=$email"
    }

    object Profile {
        const val PROFILE = "${NavigationConfig.DEEP_LINK_PREFIX}profile"
        const val PROFILE_EDIT = "${NavigationConfig.DEEP_LINK_PREFIX}profile/edit"
        const val PROFILE_SETTINGS = "${NavigationConfig.DEEP_LINK_PREFIX}profile/settings"

        fun profileWithId(userId: String) = "$PROFILE/$userId"
        fun profileEdit(userId: String) = "$PROFILE_EDIT/$userId"
    }

    object Home {
        const val MAIN = "${NavigationConfig.DEEP_LINK_PREFIX}home"
        const val SEARCH = "${NavigationConfig.DEEP_LINK_PREFIX}home/search"
        const val FAVORITES = "${NavigationConfig.DEEP_LINK_PREFIX}home/favorites"

        fun searchWithQuery(query: String) = "$SEARCH?q=$query"
    }
}