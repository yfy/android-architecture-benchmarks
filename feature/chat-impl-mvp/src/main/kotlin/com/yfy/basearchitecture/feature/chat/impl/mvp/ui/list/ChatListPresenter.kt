package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.list

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.base.BasePresenterImpl
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import com.yfy.basearchitecture.feature.chat.impl.mvp.domain.usecase.GetChatListUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListPresenter @Inject constructor(
    navigationManager: NavigationManager,
    private val getChatListUseCase: GetChatListUseCase,
    private val navigation: ChatNavigation
) : BasePresenterImpl(navigationManager) {
    private var view: ChatListView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var simulationJob: Job? = null
    private var typingSimulationJob: Job? = null

    private var cachedChats =
        mutableListOf<ChatPreview>()

    fun attachView(view: ChatListView) {
        this.view = view
        if (cachedChats.isNotEmpty()) {
            view.showChats(cachedChats)
        }

        if (cachedChats.isEmpty()) {
            loadChats()
        }
        startRealtimeSimulation()
    }

    fun detachView() {
        simulationJob?.cancel()
        typingSimulationJob?.cancel()
        view = null
    }

    override fun onViewCreated() {
        super.onViewCreated()
        logScreen("ChatListScreen")
    }

    override fun onViewDestroyed() {
        super.onViewDestroyed()
        detachView()
    }

    private fun loadChats() {
        scope.launch {
            setLoading(true)
            view?.showLoading()

            getChatListUseCase(Unit)
                .catch { error ->
                    setLoading(false)
                    view?.showError(error.localizedMessage ?: "Unknown error")
                }
                .collect { chats ->
                    setLoading(false)
                    cachedChats.clear()
                    cachedChats.addAll(chats)
                    view?.showChats(chats)
                }

        }
    }

    private fun startRealtimeSimulation() {
        simulationJob = scope.launch {
            while (isActive) {
                delay(3000)
            }
        }

        typingSimulationJob = scope.launch {
            while (isActive) {
                delay(5000)
            }
        }
    }

    fun onChatClick(chatId: String) {
        navigation.navigateToChatDetail(chatId)
    }

    fun simulateMessageUpdate(chatId: String) {
        scope.launch {
            val updatedChat = ChatPreview(
                id = chatId,
                name = "Contact $chatId",
                avatarUrl = "",
                lastMessage = "Yeni mesaj ${System.currentTimeMillis() % 100}",
                lastMessageTime = System.currentTimeMillis(),
                unreadCount = 1,
                isOnline = true,
                isTyping = false
            )
            view?.updateChat(updatedChat)
        }
    }

    fun simulateTypingUpdate(chatId: String, isTyping: Boolean) {
        scope.launch {
            val updatedChat = ChatPreview(
                id = chatId,
                name = "Contact $chatId",
                avatarUrl = "",
                lastMessage = "Last message",
                lastMessageTime = System.currentTimeMillis(),
                unreadCount = 0,
                isOnline = true,
                isTyping = isTyping
            )
            view?.updateChat(updatedChat)
        }
    }
}
