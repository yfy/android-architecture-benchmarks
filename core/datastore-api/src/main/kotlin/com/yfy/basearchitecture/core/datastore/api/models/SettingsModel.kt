package com.yfy.basearchitecture.core.datastore.api.models

import kotlinx.serialization.Serializable


@Serializable
data class ThemeSettingsData(
    val themeMode: String = "SYSTEM",
    val accentColor: String = "BLUE",
    val fontSize: String = "MEDIUM",
    val isCompactMode: Boolean = false
)

@Serializable
data class NotificationSettingsData(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val inAppEnabled: Boolean = true,
    val categories: Set<String> = emptySet(),
    val doNotDisturbStart: String? = null,
    val doNotDisturbEnd: String? = null
)

@Serializable
data class PrivacySettingsData(
    val profileVisibility: String = "PRIVATE",
    val dataSharing: Boolean = false,
    val analyticsOptOut: Boolean = false,
    val crashReporting: Boolean = true,
    val locationSharing: Boolean = false,
    val contactSync: Boolean = false
)

@Serializable
data class LanguageSettingsData(
    val appLanguage: String = "tr",
    val dateFormat: String = "dd/MM/yyyy",
    val numberFormat: String = "###.###,##",
    val currency: String = "TRY"
)

@Serializable
data class AppSettingsData(
    val autoUpdate: Boolean = true,
    val backgroundSync: Boolean = true,
    val dataUsageLimit: Long = 100 * 1024 * 1024,
    val offlineMode: Boolean = false,
    val cacheSize: Long = 50 * 1024 * 1024
)

@Serializable
data class AllSettingsData(
    val themeSettings: ThemeSettingsData,
    val notificationSettings: NotificationSettingsData,
    val privacySettings: PrivacySettingsData,
    val languageSettings: LanguageSettingsData,
    val appSettings: AppSettingsData
)