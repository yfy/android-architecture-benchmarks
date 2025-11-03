package com.yfy.basearchitecture.feature.chat.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatPreview(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false
)

@Serializable
data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isSent: Boolean = true,
    val isMine: Boolean
)
