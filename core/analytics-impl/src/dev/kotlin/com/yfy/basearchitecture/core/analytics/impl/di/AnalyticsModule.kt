package com.yfy.basearchitecture.core.analytics.impl.di

import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.analytics.impl.AnalyticsManager
import com.yfy.basearchitecture.core.analytics.impl.DebugAnalyticsProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {

    @Binds
    abstract fun bindAnalyticsTracker(analyticsManager: AnalyticsManager): AnalyticsProvider

    @Module
    @InstallIn(SingletonComponent::class)
    object AnalyticsProvidersModule {

        @Provides
        @IntoSet
        fun provideAnalyticsProvider(
            debugProvider: DebugAnalyticsProvider
        ): AnalyticsProvider = debugProvider
    }
}