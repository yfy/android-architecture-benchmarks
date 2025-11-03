package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.detail

import com.yfy.basearchitecture.core.ui.api.base.BasePresenterImpl
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.model.Message
import com.yfy.basearchitecture.feature.chat.impl.mvp.domain.usecase.GetChatMessagesUseCase
import com.yfy.basearchitecture.feature.chat.impl.mvp.domain.usecase.MarkAsReadUseCase
import com.yfy.basearchitecture.feature.chat.impl.mvp.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatDetailPresenter @Inject constructor(
    navigationManager: com.yfy.basearchitecture.core.navigation.NavigationManager,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase,
    private val navigation: ChatNavigation
) : BasePresenterImpl(navigationManager) {
    private var view: ChatDetailView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var streamJob: Job? = null
    private var currentChatId: String = ""
    private var messageCount = 0
    
    fun attachView(view: ChatDetailView, chatId: String) {
        this.view = view
        this.currentChatId = chatId
        loadMessages(chatId)
        startMessageStream()
    }
    
    fun detachView() {
        streamJob?.cancel()
        view = null
    }
    
    override fun onViewCreated() {
        super.onViewCreated()
        logScreen("ChatDetailScreen")
    }
    
    override fun onViewDestroyed() {
        super.onViewDestroyed()
        detachView()
    }
    
    private fun loadMessages(chatId: String) {
        scope.launch {
            try {
                setLoading(true)
                view?.showLoading()
                
                getChatMessagesUseCase(chatId)
                    .catch { error ->
                        setLoading(false)
                        view?.showError(error.localizedMessage ?: "Unknown error")
                    }
                    .collect { messages ->
                        setLoading(false)
                        view?.showMessages(messages)
                        view?.showContactInfo("Contact $chatId", true)
                    }
            } finally {
                setLoading(false)
                view?.hideLoading()
            }
        }
        
        scope.launch {
            markAsReadUseCase(MarkAsReadUseCase.Parameters(chatId))
        }
    }
    
    private fun startMessageStream() {
        streamJob = scope.launch {
            repeat(100) {
                delay(500)
                messageCount++
                val newMessage = Message(
                    id = "msg_${System.currentTimeMillis()}",
                    chatId = currentChatId,
                    senderId = "contact",
                    content = "Mesaj $messageCount",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    isSent = true,
                    isMine = messageCount % 2 == 0
                )
                view?.addNewMessage(newMessage)
                view?.scrollToBottom()
            }
        }
    }
    
    fun sendMessage(inputText: String) {
        val text = inputText.trim()
        if (text.isEmpty()) return
        
        scope.launch {
            try {
                sendMessageUseCase(SendMessageUseCase.Parameters(currentChatId, text))
                view?.clearInput()
                view?.scrollToBottom()
            } catch (e: Exception) {
                view?.showError("Failed to send message")
            }
        }
    }
    
    fun navigateBack() {
        navigation.navigateBack()
    }
}
