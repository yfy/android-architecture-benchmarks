package com.yfy.basearchitecture.core.navigation.constants

object Routes {

    object Auth {
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val FORGOT_PASSWORD = "forgot_password"

        fun loginWithArgs(email: String? = null) = if (email != null) "$LOGIN?email=$email" else LOGIN
    }

    object Profile {
        const val PROFILE = "profile"
        const val PROFILE_EDIT = "profile_edit"
        const val PROFILE_SETTINGS = "profile_settings"

        fun profileWithId(userId: String) = "$PROFILE/$userId"
        fun profileEdit(userId: String) = "$PROFILE_EDIT/$userId"
    }

    object Home {
        const val MAIN = "home"
        const val SEARCH = "search"
        const val FAVORITES = "favorites"

        fun searchWithQuery(query: String) = "$SEARCH?q=$query"
    }
}