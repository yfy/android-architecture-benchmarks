package com.yfy.basearchitecture.feature.chat.impl.mvc.navigation

object ChatDestinations {
    const val CHAT_LIST = "chat_list"
    const val CHAT_DETAIL = "chat_detail/{chatId}"
    fun chatDetailRoute(chatId: String) = "chat_detail/$chatId"
}
