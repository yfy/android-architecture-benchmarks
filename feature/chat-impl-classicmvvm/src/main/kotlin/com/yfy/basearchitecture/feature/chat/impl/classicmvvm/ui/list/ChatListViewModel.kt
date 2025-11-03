package com.yfy.basearchitecture.feature.chat.impl.classicmvvm.ui.list

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import com.yfy.basearchitecture.feature.chat.impl.classicmvvm.domain.usecase.GetChatListUseCase
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
class ChatListViewModel @Inject constructor(
    private val getChatListUseCase: GetChatListUseCase,
    private val navigation: ChatNavigation
) : BaseComposeViewModel() {
    private val _chats = MutableStateFlow<List<ChatPreview>>(emptyList())
    val chats: StateFlow<List<ChatPreview>> = _chats.asStateFlow()

    init {
        loadChats()
        startRealtimeSimulation()
    }

    private fun loadChats() {
        serviceLaunch {
            getChatListUseCase(Unit)
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .catch { handleError(it) }
                .collect { chats ->
                    setLoading(false)
                    _chats.value = chats
                }
        }
    }

    private fun startRealtimeSimulation() {
        serviceLaunch {
            while (true) {
                delay(3000)
                val chats = _chats.value
                if (chats.isNotEmpty()) {
                    val randomChat = chats.random()
                    _chats.value = chats.map { chat ->
                        if (chat.id == randomChat.id) {
                            chat.copy(
                                lastMessage = "Yeni mesaj ${System.currentTimeMillis() % 100}",
                                lastMessageTime = System.currentTimeMillis(),
                                unreadCount = chat.unreadCount + 1
                            )
                        } else chat
                    }.sortedByDescending { it.lastMessageTime }
                }
            }
        }
        serviceLaunch {
            while (true) {
                delay(5000)
                val chats = _chats.value
                if (chats.isNotEmpty()) {
                    val randomChat = chats.random()
                    _chats.value = chats.map { chat -> 
                        if (chat.id == randomChat.id) chat.copy(isTyping = true) else chat
                    }
                    delay(2000)
                    _chats.value = chats.map { chat -> 
                        if (chat.id == randomChat.id) chat.copy(isTyping = false) else chat
                    }
                }
            }
        }
    }

    fun onChatClick(chatId: String) {
        navigation.navigateToChatDetail(chatId)
    }
}
