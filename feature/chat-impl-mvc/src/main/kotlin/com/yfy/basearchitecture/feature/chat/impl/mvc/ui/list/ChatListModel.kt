package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.list

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcModel
import com.yfy.basearchitecture.feature.chat.impl.mvc.domain.usecase.GetChatListUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListModel @Inject constructor(
    private val getChatListUseCase: GetChatListUseCase
) : BaseMvcModel() {
    
    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()
    
    suspend fun loadChats() {
        _state.update { it.copy(isLoading = true) }
        try {
            val result = getChatListUseCase(Unit)
            result.collect { chats ->
                _state.update { it.copy(chats = chats, isLoading = false) }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }
    
    fun startRealtimeSimulation() {
        modelScope.launch {
            while (true) {
                delay(3000)
                val chats = _state.value.chats
                if (chats.isNotEmpty()) {
                    val randomChat = chats.random()
                    _state.update {
                        it.copy(
                            chats = it.chats.map { chat ->
                                if (chat.id == randomChat.id) {
                                    chat.copy(
                                        lastMessage = "Yeni mesaj ${System.currentTimeMillis() % 100}",
                                        lastMessageTime = System.currentTimeMillis(),
                                        unreadCount = chat.unreadCount + 1
                                    )
                                } else chat
                            }.sortedByDescending { it.lastMessageTime }
                        )
                    }
                }
            }
        }
    }
}
