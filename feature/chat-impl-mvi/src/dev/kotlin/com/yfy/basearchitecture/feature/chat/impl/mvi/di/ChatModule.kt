package com.yfy.basearchitecture.feature.chat.impl.mvi.di

import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.impl.mvi.data.repository.DevChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun provideChatRepository(
        impl: DevChatRepositoryImpl
    ): ChatRepository
}
