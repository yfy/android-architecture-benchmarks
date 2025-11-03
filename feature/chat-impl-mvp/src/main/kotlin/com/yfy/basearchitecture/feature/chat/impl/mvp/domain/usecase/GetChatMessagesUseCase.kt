package com.yfy.basearchitecture.feature.chat.impl.mvp.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.api.model.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseFlowUseCase<String, List<Message>>() {
    
    override fun execute(parameters: String): Flow<List<Message>> {
        return chatRepository.getChatMessages(parameters)
    }
}
