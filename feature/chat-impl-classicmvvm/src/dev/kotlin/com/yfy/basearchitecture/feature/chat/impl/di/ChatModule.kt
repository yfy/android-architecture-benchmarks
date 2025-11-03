package com.yfy.basearchitecture.feature.chat.impl.di

import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.impl.data.repository.DevChatRepositoryImpl
import com.yfy.basearchitecture.feature.chat.impl.navigation.ChatNavGraphBuilder
import com.yfy.basearchitecture.feature.chat.impl.navigation.ChatNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        devChatRepositoryImpl: DevChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindChatNavigation(
        chatNavigationImpl: ChatNavigationImpl
    ): ChatNavigation

    companion object {
        @Provides
        @IntoSet
        fun provideChatNavGraphBuilder(
            chatNavGraphBuilder: ChatNavGraphBuilder
        ): FeatureNavGraphBuilder = chatNavGraphBuilder
    }
}
