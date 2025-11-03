package com.yfy.basearchitecture.core.datastore.api.interfaces

import com.yfy.basearchitecture.core.datastore.api.models.AllSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.AppSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.LanguageSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.NotificationSettingsData
import com.yfy.basearchitecture.core.datastore.api.models.PrivacySettingsData
import com.yfy.basearchitecture.core.datastore.api.models.ThemeSettingsData
import kotlinx.coroutines.flow.Flow

interface AppSettingsManager {

    fun getAllSettings(): Flow<AllSettingsData>

    suspend fun saveThemeSettings(settings: ThemeSettingsData)
    fun getThemeSettings(): Flow<ThemeSettingsData>

    suspend fun saveNotificationSettings(settings: NotificationSettingsData)
    fun getNotificationSettings(): Flow<NotificationSettingsData>

    suspend fun savePrivacySettings(settings: PrivacySettingsData)
    fun getPrivacySettings(): Flow<PrivacySettingsData>

    suspend fun saveLanguageSettings(settings: LanguageSettingsData)
    fun getLanguageSettings(): Flow<LanguageSettingsData>

    suspend fun saveAppSettings(settings: AppSettingsData)
    fun getAppSettings(): Flow<AppSettingsData>

    suspend fun setAppVersion(version: String)
    fun getAppVersion(): Flow<String>

    suspend fun setFirstLaunch(isFirst: Boolean)
    fun isFirstLaunch(): Flow<Boolean>

    suspend fun setOnboardingCompleted(completed: Boolean)
    fun isOnboardingCompleted(): Flow<Boolean>

    suspend fun clearAll()
}