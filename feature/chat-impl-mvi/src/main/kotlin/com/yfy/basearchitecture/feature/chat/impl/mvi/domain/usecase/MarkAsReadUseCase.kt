package com.yfy.basearchitecture.feature.chat.impl.mvi.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.chat.api.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkAsReadUseCase @Inject constructor(
    private val chatRepository: ChatRepository
): BaseFlowUseCase<MarkAsReadUseCase.Parameters, Unit>() {

    data class Parameters(
        val chatId: String
    )

    override fun execute(parameters: Parameters): Flow<Unit> {
       return chatRepository.markAsRead(parameters.chatId)
    }
}
