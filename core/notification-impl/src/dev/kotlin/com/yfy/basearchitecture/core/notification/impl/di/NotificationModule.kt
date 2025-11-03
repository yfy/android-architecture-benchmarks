package com.yfy.basearchitecture.core.notification.impl.di

import com.yfy.basearchitecture.core.notification.api.domain.NotificationProvider
import com.yfy.basearchitecture.core.notification.api.domain.NotificationRepository
import com.yfy.basearchitecture.core.notification.impl.data.repository.NotificationRepositoryImpl
import com.yfy.basearchitecture.core.notification.impl.domain.NotificationProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for notification dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    
    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository {
        return notificationRepositoryImpl
    }
    
    @Provides
    @Singleton
    fun provideNotificationProvider(
        notificationProviderImpl: NotificationProviderImpl
    ): NotificationProvider {
        return notificationProviderImpl
    }
}