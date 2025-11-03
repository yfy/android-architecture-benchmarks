package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.detail

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcController
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatDetailController @Inject constructor(
    model: ChatDetailModel,
    private val navigation: ChatNavigation,
    chatId: String
) : BaseMvcController<ChatDetailModel>(model) {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        scope.launch {
            model.loadMessages(chatId)
        }
        model.startMessageStream()
    }
    
    fun onInputChange(text: String) {
        model.updateInput(text)
    }
    
    fun onSendMessage() {
        scope.launch { model.sendMessage() }
    }
    
    fun onBackClick() {
        navigation.navigateBack()
    }
    
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
