package com.yfy.basearchitecture.feature.chat.impl.mvi.ui.detail

sealed interface ChatDetailIntent {
    data class LoadMessages(val chatId: String) : ChatDetailIntent
    data class InputChanged(val text: String) : ChatDetailIntent
    object SendMessage : ChatDetailIntent
    object NavigateBack : ChatDetailIntent
}
