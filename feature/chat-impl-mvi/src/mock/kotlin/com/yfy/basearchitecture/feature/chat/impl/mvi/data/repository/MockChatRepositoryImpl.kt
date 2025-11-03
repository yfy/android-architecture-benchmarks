package com.yfy.basearchitecture.feature.chat.impl.mvi.data.repository

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.base.BaseRepository
import com.yfy.basearchitecture.core.ui.api.extensions.getJson
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import com.yfy.basearchitecture.feature.chat.api.model.Message
import com.yfy.basearchitecture.feature.chat.impl.mvi.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockChatRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseRepository(), ChatRepository {

    private val _chatPreviews = MutableStateFlow<List<ChatPreview>>(emptyList())
    private val _messagesByChatId = mutableMapOf<String, MutableStateFlow<List<Message>>>()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val chats: List<ChatPreview> = context.getJson(file = R.raw.chat_previews)
        _chatPreviews.value = chats
    }

    override fun getChatList(): Flow<List<ChatPreview>> = _chatPreviews

    override fun getChatMessages(chatId: String): Flow<List<Message>> {
        if (!_messagesByChatId.containsKey(chatId)) {
            val messages: List<Message> = context.getJson(file = R.raw.chat_messages)
            _messagesByChatId[chatId] = MutableStateFlow(messages.filter { it.chatId == chatId })
        }
        return _messagesByChatId[chatId]!!
    }

    override fun sendMessage(chatId: String, text: String): Flow<Unit> = flow {
//        delay(300)
        val newMessage = Message(
            id = "msg_${System.currentTimeMillis()}",
            chatId = chatId,
            senderId = "me",
            content = text,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            isSent = true,
            isMine = true
        )
        val currentMessages = _messagesByChatId[chatId]?.value ?: emptyList()
        _messagesByChatId[chatId]?.value = currentMessages + newMessage
        _chatPreviews.value = _chatPreviews.value.map { chat ->
            if (chat.id == chatId) {
                chat.copy(
                    lastMessage = text, 
                    lastMessageTime = System.currentTimeMillis()
                )
            } else chat
        }.sortedByDescending { it.lastMessageTime }
    }

    override fun markAsRead(chatId: String): Flow<Unit> = flow {
        //delay(100)
        _chatPreviews.value = _chatPreviews.value.map { chat ->
            if (chat.id == chatId) chat.copy(unreadCount = 0) else chat
        }
    }
}
