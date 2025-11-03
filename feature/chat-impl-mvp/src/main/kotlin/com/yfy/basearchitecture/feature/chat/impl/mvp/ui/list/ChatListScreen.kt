package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yfy.basearchitecture.core.ui.api.base.BaseScreenScaffoldMvp
import com.yfy.basearchitecture.feature.chat.api.model.ChatPreview
import com.yfy.basearchitecture.feature.chat.impl.mvp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(
    presenter: ChatListPresenter = hiltViewModel<ChatListPresenterWrapper>().presenter
) {
    // State holders (replacing ViewModel state)
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // View implementation
    val view = remember {
        object : ChatListView {
            override fun showChats(newChats: List<ChatPreview>) {
                chats = newChats
            }
            override fun updateChat(updatedChat: ChatPreview) {
                chats = chats.map { chat ->
                    if (chat.id == updatedChat.id) updatedChat else chat
                }.sortedByDescending { it.lastMessageTime }
            }
            override fun showLoading() { isLoading = true }
            override fun hideLoading() { isLoading = false }
            override fun showError(message: String) { error = message }
        }
    }
    
    // Lifecycle management
    DisposableEffect(Unit) {
        presenter.attachView(view)
        onDispose { presenter.detachView() }
    }
    
    // Real-time simulation
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            if (chats.isNotEmpty()) {
                val randomChat = chats.random()
                val updatedChat = randomChat.copy(
                    lastMessage = "Yeni mesaj ${System.currentTimeMillis() % 100}",
                    lastMessageTime = System.currentTimeMillis(),
                    unreadCount = randomChat.unreadCount + 1
                )
                view.updateChat(updatedChat)
            }
        }
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            if (chats.isNotEmpty()) {
                val randomChat = chats.random()
                val typingChat = randomChat.copy(isTyping = true)
                view.updateChat(typingChat)
                kotlinx.coroutines.delay(2000)
                val notTypingChat = randomChat.copy(isTyping = false)
                view.updateChat(notTypingChat)
            }
        }
    }
    
    // BaseScreenScaffoldMvp for consistency
    BaseScreenScaffoldMvp(
        presenter = presenter,
        screenName = "ChatListScreen"
    ) {
        // Copy exact ChatListContent from MVVM version
        ChatListContent(
            chats = chats,
            isLoading = isLoading,
            onChatClick = presenter::onChatClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListContent(
    chats: List<ChatPreview>,
    isLoading: Boolean,
    onChatClick: (String) -> Unit, 
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { Text(context.getString(R.string.feature_chat_impl_chat_list_title)) }) }) { paddingValues ->
        LazyColumn(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            items(chats, key = { it.id }) { chat ->
                ChatPreviewItem(chat = chat, onClick = { onChatClick(chat.id) })
                HorizontalDivider()
            }
        }
        if (chats.isEmpty() && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Henüz sohbet yok")
            }
        }
    }
}

@Composable
private fun ChatPreviewItem(
    chat: ChatPreview, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { 
            Text(
                chat.name,
                fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
            ) 
        },
        supportingContent = {
            Text(
                text = if (chat.isTyping) "yazıyor..." else chat.lastMessage,
                style = if (chat.isTyping) MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic) 
                else MaterialTheme.typography.bodyMedium,
                color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onSurface 
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Box {
                AsyncImage(
                    model = chat.avatarUrl,
                    contentDescription = chat.name,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (chat.isOnline) {
                    Box(
                        modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF4CAF50))
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatTimestamp(chat.lastMessageTime), 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.size(20.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(), 
                            style = MaterialTheme.typography.labelSmall, 
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "Şimdi"
        diff < 3600000 -> "${diff / 60000}dk"
        diff < 86400000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        diff < 172800000 -> "Dün"
        else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
    }
}
