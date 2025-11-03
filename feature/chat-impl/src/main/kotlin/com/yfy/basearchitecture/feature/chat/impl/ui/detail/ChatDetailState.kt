package com.yfy.basearchitecture.feature.chat.impl.ui.detail

import com.yfy.basearchitecture.feature.chat.api.model.Message

data class ChatDetailState(
    val contactName: String = "",
    val isOnline: Boolean = false,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val autoScrollToBottom: Boolean = true
)
