package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.list

import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview

data class ChatListState(
    val chats: List<ChatPreview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
