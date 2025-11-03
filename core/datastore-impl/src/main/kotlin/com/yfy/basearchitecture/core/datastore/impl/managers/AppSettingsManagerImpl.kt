package com.yfy.basearchitecture.core.datastore.impl.managers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.yfy.basearchitecture.core.datastore.api.PreferenceKeys
import com.yfy.basearchitecture.core.datastore.api.interfaces.AppSettingsManager
import com.yfy.basearchitecture.core.datastore.api.interfaces.PreferenceManager
import com.yfy.basearchitecture.core.datastore.api.models.AllSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.AppSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.LanguageSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.NotificationSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.PrivacySettingsData
import com.yfy.basearchitecture.core.datastore.api.models.ThemeSettingsData
import com.yfy.basearchitecture.core.datastore.impl.di.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsManagerImpl @Inject constructor(
    @SettingsDataStore private val dataStore: DataStore<Preferences>,
    private val preferenceManager: PreferenceManager
) : AppSettingsManager {

    override fun getAllSettings(): Flow<AllSettingsData> {
        return combine(
            getThemeSettings(),
            getNotificationSettings(),
            getPrivacySettings(),
            getLanguageSettings(),
            getAppSettings()
        ) { theme, notification, privacy, language, app ->
            AllSettingsData(
                themeSettings = theme,
                notificationSettings = notification,
                privacySettings = privacy,
                languageSettings = language,
                appSettings = app
            )
        }
    }

    override suspend fun saveThemeSettings(settings: ThemeSettingsData) {
        preferenceManager.setObject(
            PreferenceKeys.THEME_SETTINGS,
            settings,
            ThemeSettingsData.serializer()
        )
    }

    override fun getThemeSettings(): Flow<ThemeSettingsData> {
        return preferenceManager.getObject(
            PreferenceKeys.THEME_SETTINGS,
            ThemeSettingsData.serializer(),
            ThemeSettingsData()
        )
    }

    override suspend fun saveNotificationSettings(settings: NotificationSettingsData) {
        preferenceManager.setObject(
            PreferenceKeys.NOTIFICATION_SETTINGS,
            settings,
            NotificationSettingsData.serializer()
        )
    }

    override fun getNotificationSettings(): Flow<NotificationSettingsData> {
        return preferenceManager.getObject(
            PreferenceKeys.NOTIFICATION_SETTINGS,
            NotificationSettingsData.serializer(),
            NotificationSettingsData()
        )
    }

    override suspend fun savePrivacySettings(settings: PrivacySettingsData) {
        preferenceManager.setObject(
            PreferenceKeys.PRIVACY_SETTINGS,
            settings,
            PrivacySettingsData.serializer()
        )
    }

    override fun getPrivacySettings(): Flow<PrivacySettingsData> {
        return preferenceManager.getObject(
            PreferenceKeys.PRIVACY_SETTINGS,
            PrivacySettingsData.serializer(),
            PrivacySettingsData()
        )
    }

    override suspend fun saveLanguageSettings(settings: LanguageSettingsData) {
        preferenceManager.setObject(
            PreferenceKeys.LANGUAGE_SETTINGS,
            settings,
            LanguageSettingsData.serializer()
        )
    }

    override fun getLanguageSettings(): Flow<LanguageSettingsData> {
        return preferenceManager.getObject(
            PreferenceKeys.LANGUAGE_SETTINGS,
            LanguageSettingsData.serializer(),
            LanguageSettingsData()
        )
    }

    override suspend fun saveAppSettings(settings: AppSettingsData) {
        preferenceManager.setObject(
            PreferenceKeys.APP_SETTINGS,
            settings,
            AppSettingsData.serializer()
        )
    }

    override fun getAppSettings(): Flow<AppSettingsData> {
        return preferenceManager.getObject(
            PreferenceKeys.APP_SETTINGS,
            AppSettingsData.serializer(),
            AppSettingsData()
        )
    }

    override suspend fun setAppVersion(version: String) {
        preferenceManager.setString(PreferenceKeys.APP_VERSION, version)
    }

    override fun getAppVersion(): Flow<String> {
        return preferenceManager.getString(PreferenceKeys.APP_VERSION, "1.0.0")
    }

    override suspend fun setFirstLaunch(isFirst: Boolean) {
        preferenceManager.setBoolean(PreferenceKeys.FIRST_LAUNCH, isFirst)
    }

    override fun isFirstLaunch(): Flow<Boolean> {
        return preferenceManager.getBoolean(PreferenceKeys.FIRST_LAUNCH, true)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferenceManager.setBoolean(PreferenceKeys.ONBOARDING_COMPLETED, completed)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return preferenceManager.getBoolean(PreferenceKeys.ONBOARDING_COMPLETED, false)
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}