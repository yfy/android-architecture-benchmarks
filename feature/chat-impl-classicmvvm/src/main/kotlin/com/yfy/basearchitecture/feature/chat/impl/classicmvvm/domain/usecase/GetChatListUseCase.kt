package com.yfy.basearchitecture.feature.chat.impl.classicmvvm.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChatListUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseFlowUseCase<Unit, List<ChatPreview>>() {
    
    override fun execute(parameters: Unit): Flow<List<ChatPreview>> {
        return chatRepository.getChatList()
    }
}
