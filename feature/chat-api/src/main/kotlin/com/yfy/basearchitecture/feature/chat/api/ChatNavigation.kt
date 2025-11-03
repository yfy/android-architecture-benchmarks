package com.yfy.basearchitecture.feature.chat.api

interface ChatNavigation {
    fun navigateToChatList()
    fun navigateToChatDetail(chatId: String)
    fun navigateBack()
}
