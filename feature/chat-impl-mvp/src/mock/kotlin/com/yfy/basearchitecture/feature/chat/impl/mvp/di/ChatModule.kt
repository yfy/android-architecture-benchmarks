package com.yfy.basearchitecture.feature.chat.impl.mvp.di

import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.impl.mvp.repository.MockChatRepositoryImpl
import com.yfy.basearchitecture.feature.chat.impl.mvp.navigation.ChatNavGraphBuilder
import com.yfy.basearchitecture.feature.chat.impl.mvp.navigation.ChatNavigationImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(impl: MockChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideChatNavigation(impl: ChatNavigationImpl): ChatNavigation = impl

    @Provides
    @IntoSet
    fun provideFeatureNavGraphBuilder(builder: ChatNavGraphBuilder): FeatureNavGraphBuilder = builder
}
