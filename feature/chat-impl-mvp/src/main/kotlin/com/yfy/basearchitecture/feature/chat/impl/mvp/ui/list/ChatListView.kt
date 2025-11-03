package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.list

import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview

interface ChatListView {
    fun showChats(chats: List<ChatPreview>)
    fun updateChat(chat: ChatPreview) // For real-time updates
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
}
