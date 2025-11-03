package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.detail

import com.yfy.basearchitecture.feature.chat.api.model.Message

data class ChatDetailState(
    val chatId: String = "",
    val contactName: String = "",
    val isOnline: Boolean = false,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val autoScrollToBottom: Boolean = true
)
