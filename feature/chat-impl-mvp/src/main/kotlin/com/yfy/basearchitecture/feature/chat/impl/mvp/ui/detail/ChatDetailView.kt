package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.detail

import com.yfy.basearchitecture.feature.chat.api.model.Message

interface ChatDetailView {
    fun showMessages(messages: List<Message>)
    fun addNewMessage(message: Message) // For real-time stream
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun clearInput()
    fun scrollToBottom()
    fun showContactInfo(name: String, isOnline: Boolean)
}
