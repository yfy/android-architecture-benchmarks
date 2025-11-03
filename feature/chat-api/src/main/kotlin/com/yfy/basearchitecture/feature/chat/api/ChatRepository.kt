package com.yfy.basearchitecture.feature.chat.api

import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import com.yfy.basearchitecture.feature.chat.api.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatList(): Flow<List<ChatPreview>>
    fun getChatMessages(chatId: String): Flow<List<Message>>
    fun sendMessage(chatId: String, text: String) : Flow<Unit>
    fun markAsRead(chatId: String): Flow<Unit>
}
