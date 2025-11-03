package com.yfy.basearchitecture.core.datastore.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yfy.basearchitecture.core.datastore.api.interfaces.AppSettingsManager
import com.yfy.basearchitecture.core.datastore.api.interfaces.CacheManager
import com.yfy.basearchitecture.core.datastore.api.interfaces.PreferenceManager
import com.yfy.basearchitecture.core.datastore.api.interfaces.UserSessionManager
import com.yfy.basearchitecture.core.datastore.impl.managers.AppSettingsManagerImpl
import com.yfy.basearchitecture.core.datastore.impl.managers.CacheManagerImpl
import com.yfy.basearchitecture.core.datastore.impl.managers.PreferenceManagerImpl
import com.yfy.basearchitecture.core.datastore.impl.managers.UserSessionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CacheDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SessionDataStore

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences"
)

private val Context.cacheDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_cache"
)

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session"
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    @PreferencesDataStore
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.preferencesDataStore
    }

    @Provides
    @Singleton
    @CacheDataStore
    fun provideCacheDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.cacheDataStore
    }

    @Provides
    @Singleton
    @SettingsDataStore
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingsDataStore
    }

    @Provides
    @Singleton
    @SessionDataStore
    fun provideSessionDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.sessionDataStore
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreBindingModule {

    @Binds
    abstract fun bindPreferenceManager(
        preferenceManagerImpl: PreferenceManagerImpl
    ): PreferenceManager

    @Binds
    abstract fun bindCacheManager(
        cacheManagerImpl: CacheManagerImpl
    ): CacheManager

    @Binds
    abstract fun bindAppSettingsManager(
        appSettingsManagerImpl: AppSettingsManagerImpl
    ): AppSettingsManager

    @Binds
    abstract fun bindUserSessionManager(
        userSessionManagerImpl: UserSessionManagerImpl
    ): UserSessionManager
}