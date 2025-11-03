package com.yfy.basearchitecture.feature.chat.impl.mvi.ui.list

sealed interface ChatListIntent {
    object LoadChats : ChatListIntent
    data class ChatClicked(val chatId: String) : ChatListIntent
}
