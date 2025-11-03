package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.detail

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcModel
import com.yfy.basearchitecture.feature.chat.api.model.Message
import com.yfy.basearchitecture.feature.chat.impl.mvc.domain.usecase.GetChatMessagesUseCase
import com.yfy.basearchitecture.feature.chat.impl.mvc.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatDetailModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : BaseMvcModel() {
    
    private val _state = MutableStateFlow(ChatDetailState())
    val state: StateFlow<ChatDetailState> = _state.asStateFlow()
    
    suspend fun loadMessages(chatId: String) {
        _state.update { it.copy(isLoading = true, chatId = chatId) }
        try {
            val result = getChatMessagesUseCase(GetChatMessagesUseCase.Parameters(chatId))
            result.first().let { messages ->
                _state.update { it.copy(messages = messages, isLoading = false) }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }
    
    fun startMessageStream() {
        modelScope.launch {
            repeat(100) {
                delay(500)
                val newMessage = Message(
                    id = "msg_${System.currentTimeMillis()}",
                    chatId = _state.value.chatId,
                    senderId = "contact",
                    content = "Message ${it + 1}",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    isSent = true,
                    isMine = it % 2 == 0
                )
                _state.update { it.copy(messages = it.messages + newMessage) }
            }
        }
    }
    
    fun updateInput(text: String) {
        _state.update { it.copy(inputText = text) }
    }
    
    suspend fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty()) return
        
        _state.update { it.copy(isSending = true) }
        try {
            sendMessageUseCase(SendMessageUseCase.Parameters(_state.value.chatId, text))
            _state.update { it.copy(inputText = "", isSending = false) }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isSending = false) }
        }
    }
}
