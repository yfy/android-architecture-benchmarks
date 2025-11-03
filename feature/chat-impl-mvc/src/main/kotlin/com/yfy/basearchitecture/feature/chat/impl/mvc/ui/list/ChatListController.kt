package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.list

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcController
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListController @Inject constructor(
    model: ChatListModel,
    private val navigation: ChatNavigation
) : BaseMvcController<ChatListModel>(model) {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        scope.launch { 
            model.loadChats()
        }
        model.startRealtimeSimulation()
    }
    
    fun onChatClicked(chatId: String) {
        navigation.navigateToChatDetail(chatId)
    }
    
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
