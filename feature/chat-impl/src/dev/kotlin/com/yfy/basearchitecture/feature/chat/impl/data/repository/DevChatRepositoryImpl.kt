package com.yfy.basearchitecture.feature.chat.impl.data.repository

import com.yfy.basearchitecture.core.ui.api.base.BaseRepository
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import com.yfy.basearchitecture.feature.chat.api.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevChatRepositoryImpl @Inject constructor(
) : BaseRepository(), ChatRepository {

    private val _chatPreviews = MutableStateFlow<List<ChatPreview>>(emptyList())
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // Generate mock chat previews
        val mockChatPreviews = (1..50).map { index ->
            ChatPreview(
                id = "chat_$index",
                name = "Contact $index",
                avatarUrl = "https://i.pravatar.cc/150?img=$index",
                lastMessage = "Last message from contact $index",
                lastMessageTime = System.currentTimeMillis() - (index * 1000L),
                unreadCount = (0..5).random(),
                isOnline = index % 3 == 0,
                isTyping = false
            )
        }
        _chatPreviews.value = mockChatPreviews

        // Generate mock messages for each chat
        val messagesByChatId = mutableMapOf<String, List<Message>>()
        (1..50).forEach { chatIndex ->
            val messageCount = (5..20).random()
            val chatMessages = (1..messageCount).map { msgIndex ->
                Message(
                    id = "msg_${chatIndex}_$msgIndex",
                    chatId = "chat_$chatIndex",
                    senderId = if (msgIndex % 3 == 0) "user" else "agent",
                    text = "Message $msgIndex from chat $chatIndex",
                    timestamp = System.currentTimeMillis() - ((messageCount - msgIndex) * 1000L),
                    isRead = msgIndex > messageCount - 3,
                    isSent = msgIndex % 3 == 0,
                    isMine = msgIndex % 3 == 0
                )
            }
            messagesByChatId["chat_$chatIndex"] = chatMessages
        }
        _messages.value = messagesByChatId
    }

    override fun getChatList(): Flow<List<ChatPreview>> = _chatPreviews.asStateFlow()

    override fun getChatMessages(chatId: String): Flow<List<Message>> = _messages.asStateFlow().map { messagesMap ->
        messagesMap[chatId] ?: emptyList()
    }

    override fun sendMessage(chatId: String, text: String): Flow<Unit> = flow {
        val newMessage = Message(
            id = "msg_${System.currentTimeMillis()}",
            chatId = chatId,
            senderId = "user",
            text = text,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            isSent = true,
            isMine = true
        )

        val currentMessages = _messages.value.toMutableMap()
        val chatMessages = currentMessages[chatId]?.toMutableList() ?: mutableListOf()
        chatMessages.add(newMessage)
        currentMessages[chatId] = chatMessages
        _messages.value = currentMessages
        
        emit(Unit)
    }

    override fun markAsRead(chatId: String): Flow<Unit> = flow {
        val currentMessages = _messages.value.toMutableMap()
        val chatMessages = currentMessages[chatId]?.map { message ->
            if (!message.isMine) {
                message.copy(isRead = true)
            } else {
                message
            }
        } ?: emptyList()
        
        if (chatMessages.isNotEmpty()) {
            currentMessages[chatId] = chatMessages
            _messages.value = currentMessages
        }
        
        emit(Unit)
    }
}
