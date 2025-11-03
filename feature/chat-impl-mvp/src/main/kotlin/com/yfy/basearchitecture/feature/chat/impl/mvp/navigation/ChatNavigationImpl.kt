package com.yfy.basearchitecture.feature.chat.impl.mvp.navigation

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.feature.chat.api.ChatNavigation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatNavigationImpl @Inject constructor(
    private val navigationManager: NavigationManager
) : ChatNavigation {
    override fun navigateToChatList() {
        navigationManager.navigate(ChatDestinations.CHAT_LIST)
    }
    
    override fun navigateToChatDetail(chatId: String) {
        navigationManager.navigate(ChatDestinations.chatDetailRoute(chatId))
    }
    
    override fun navigateBack() {
        navigationManager.navigateUp()
    }
}
