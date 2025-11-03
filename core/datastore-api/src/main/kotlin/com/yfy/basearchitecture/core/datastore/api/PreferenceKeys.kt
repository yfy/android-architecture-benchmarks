package com.yfy.basearchitecture.core.datastore.api

object PreferenceKeys {

    // Settings Objects
    const val THEME_SETTINGS = "theme_settings"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val PRIVACY_SETTINGS = "privacy_settings"
    const val LANGUAGE_SETTINGS = "language_settings"
    const val APP_SETTINGS = "app_settings"

    // App Lifecycle
    const val APP_VERSION = "app_version"
    const val FIRST_LAUNCH = "first_launch"
    const val ONBOARDING_COMPLETED = "onboarding_completed"

    // Legacy (backward compatibility)
    const val LANGUAGE = "language"
    const val THEME_MODE = "theme_mode"

    // User Session (Auth modülünde olmalı)
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val USER_ID = "user_id"
    const val USERNAME = "username"
    const val BIOMETRIC_ENABLED = "biometric_enabled"
    const val AUTO_LOGIN_ENABLED = "auto_login_enabled"
    const val USER_SESSION = "user_session"
}