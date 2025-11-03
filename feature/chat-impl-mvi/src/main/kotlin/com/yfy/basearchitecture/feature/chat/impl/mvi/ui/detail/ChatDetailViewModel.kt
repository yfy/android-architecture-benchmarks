package com.yfy.basearchitecture.feature.chat.impl.mvi.ui.detail

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.model.Message
import com.yfy.basearchitecture.feature.chat.impl.mvi.domain.usecase.GetChatMessagesUseCase
import com.yfy.basearchitecture.feature.chat.impl.mvi.domain.usecase.MarkAsReadUseCase
import com.yfy.basearchitecture.feature.chat.impl.mvi.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase,
    private val navigation: ChatNavigation
) : BaseComposeViewModel() {

    private val _state = MutableStateFlow(ChatDetailState())
    val state: StateFlow<ChatDetailState> = _state.asStateFlow()
    private var currentChatId: String = ""

    fun handleIntent(intent: ChatDetailIntent) {
        when (intent) {
            is ChatDetailIntent.LoadMessages -> loadMessages(intent.chatId)
            is ChatDetailIntent.InputChanged -> onInputChange(intent.text)
            is ChatDetailIntent.SendMessage -> sendMessage()
            is ChatDetailIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadMessages(chatId: String) {
        currentChatId = chatId
        serviceLaunch {
            getChatMessagesUseCase(chatId)
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .catch { handleError(it) }
                .collect { messages ->
                    setLoading(false)
                    _state.update { 
                        it.copy(
                            messages = messages, 
                            contactName = "Contact $chatId", 
                            isOnline = true
                        ) 
                    }
                }
        }
        serviceLaunch {
            markAsReadUseCase(MarkAsReadUseCase.Parameters(chatId))
        }
        startMessageStream()
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
                _state.update { 
                    it.copy(
                        messages = it.messages + newMessage, 
                        autoScrollToBottom = true
                    ) 
                }
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty()) return
        serviceLaunch {
            _state.update { it.copy(isSending = true) }
            sendMessageUseCase(SendMessageUseCase.Parameters(currentChatId, text))
            _state.update { 
                it.copy(
                    inputText = "", 
                    isSending = false, 
                    autoScrollToBottom = true
                ) 
            }
        }
    }

    private fun onInputChange(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    private fun navigateBack() {
        navigation.navigateBack()
    }
}
