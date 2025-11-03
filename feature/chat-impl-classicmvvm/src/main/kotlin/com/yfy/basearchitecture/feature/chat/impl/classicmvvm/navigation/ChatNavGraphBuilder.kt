package com.yfy.basearchitecture.feature.chat.impl.classicmvvm.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.chat.impl.classicmvvm.ui.detail.ChatDetailScreen
import com.yfy.basearchitecture.feature.chat.impl.classicmvvm.ui.list.ChatListScreen
import javax.inject.Inject

class ChatNavGraphBuilder @Inject constructor() : FeatureNavGraphBuilder {
    override fun NavGraphBuilder.buildNavGraph(navManager: NavigationManager) {
        composable(ChatDestinations.CHAT_LIST) {
            ChatListScreen()
        }
        composable(
            route = ChatDestinations.CHAT_DETAIL,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            ChatDetailScreen(chatId = chatId)
        }
    }
}
