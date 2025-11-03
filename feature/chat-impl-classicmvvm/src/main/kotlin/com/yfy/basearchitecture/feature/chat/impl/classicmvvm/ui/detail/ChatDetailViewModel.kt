package com.yfy.basearchitecture.feature.chat.impl.classicmvvm.ui.detail

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.model.Message
import com.yfy.basearchitecture.feature.chat.impl.classicmvvm.domain.usecase.GetChatMessagesUseCase
import com.yfy.basearchitecture.feature.chat.impl.classicmvvm.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val navigation: ChatNavigation
) : BaseComposeViewModel() {

    private val _contactName = MutableStateFlow("")
    val contactName: StateFlow<String> = _contactName.asStateFlow()
    
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()
    
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()
    
    private val _autoScrollToBottom = MutableStateFlow(true)
    val autoScrollToBottom: StateFlow<Boolean> = _autoScrollToBottom.asStateFlow()

    private var currentChatId: String = ""

    fun loadMessages(chatId: String) {
        currentChatId = chatId
        serviceLaunch {
            getChatMessagesUseCase(chatId)
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .catch { handleError(it) }
                .collect { messages ->
                    setLoading(false)
                    _messages.value = messages
                    _contactName.value = "Contact $chatId"
                    _isOnline.value = true
                    startMessageStream()
                }
        }
    }

    private fun startMessageStream() {
        serviceLaunch {
            repeat(100) {
                delay(500)
                val newMessage = Message(
                    id = "msg_${System.currentTimeMillis()}",
                    chatId = currentChatId,
                    senderId = "contact",
                    content = "Mesaj ${it + 1}",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    isSent = true,
                    isMine = it % 2 == 0
                )
                _messages.value += newMessage
            }
        }
    }

    fun onInputChange(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value
        if (text.isBlank()) return
        
        serviceLaunch {
            _isSending.value = true
            sendMessageUseCase(SendMessageUseCase.Parameters("current_chat", text))
                .catch { handleError(it) }
                .collect {
                    _inputText.value = ""
                    _isSending.value = false
                }
        }
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
}
