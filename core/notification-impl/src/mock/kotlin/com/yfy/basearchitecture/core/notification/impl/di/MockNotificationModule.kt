package com.yfy.basearchitecture.core.notification.impl.di

import com.yfy.basearchitecture.core.notification.api.domain.NotificationProvider
import com.yfy.basearchitecture.core.notification.api.domain.NotificationRepository
import com.yfy.basearchitecture.core.notification.impl.domain.MockNotificationProvider
import com.yfy.basearchitecture.core.notification.impl.domain.MockNotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MockNotificationModule {
    
    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationRepository {
        return MockNotificationRepository()
    }
    
    @Provides
    @Singleton
    fun provideNotificationProvider(): NotificationProvider {
        return MockNotificationProvider()
    }
} 