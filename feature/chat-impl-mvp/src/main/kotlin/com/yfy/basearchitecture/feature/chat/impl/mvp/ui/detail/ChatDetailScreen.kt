package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yfy.basearchitecture.core.ui.api.base.BaseScreenScaffoldMvp
import com.yfy.basearchitecture.feature.chat.api.model.Message
import com.yfy.basearchitecture.feature.chat.impl.mvp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatDetailScreen(
    chatId: String, 
    presenter: ChatDetailPresenter = hiltViewModel<ChatDetailPresenterWrapper>().presenter
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var contactName by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }
    var autoScrollToBottom by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    
    val view = remember {
        object : ChatDetailView {
            override fun showMessages(newMessages: List<Message>) {
                messages = newMessages
            }
            override fun addNewMessage(message: Message) {
                messages = messages + message
                autoScrollToBottom = true
            }
            override fun showLoading() { isLoading = true }
            override fun hideLoading() { isLoading = false }
            override fun showError(message: String) { error = message }
            override fun clearInput() { 
                inputText = ""
                isSending = false
            }
            override fun scrollToBottom() { 
                autoScrollToBottom = true
            }
            override fun showContactInfo(name: String, online: Boolean) {
                contactName = name
                isOnline = online
            }
        }
    }
    
    // Lifecycle management
    DisposableEffect(chatId) {
        presenter.attachView(view, chatId)
        onDispose { presenter.detachView() }
    }
    
    LaunchedEffect(messages.size, autoScrollToBottom) {
        if (messages.isNotEmpty() && autoScrollToBottom) {
            listState.animateScrollToItem(0)
            autoScrollToBottom = false
        }
    }
    
    BaseScreenScaffoldMvp(
        presenter = presenter,
        screenName = "ChatDetailScreen"
    ) {
        ChatDetailContent(
            messages = messages,
            inputText = inputText,
            isSending = isSending,
            contactName = contactName,
            isOnline = isOnline,
            listState = listState,
            onSendMessage = { presenter.sendMessage(inputText) },
            onInputChange = { inputText = it },
            onBackClick = presenter::navigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatDetailContent(
    messages: List<Message>,
    inputText: String,
    isSending: Boolean,
    contactName: String,
    isOnline: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onSendMessage: () -> Unit, 
    onInputChange: (String) -> Unit, 
    onBackClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(contactName, style = MaterialTheme.typography.titleMedium)
                        if (isOnline) {
                            Text(
                                "çevrimiçi", 
                                style = MaterialTheme.typography.labelSmall, 
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = { 
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") 
                    } 
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        placeholder = { Text(context.getString(R.string.feature_chat_impl_type_message)) },
                        modifier = Modifier.weight(1f),
                        maxLines = 4
                    )
                    IconButton(
                        onClick = onSendMessage, 
                        enabled = inputText.isNotBlank() && !isSending
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.asReversed(), key = { it.id }) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message, 
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(), 
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (message.isMine) Color(0xFF00897B) else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium, 
                    color = if (message.isMine) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.End, 
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (message.isMine) Color.White.copy(alpha = 0.7f) 
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (message.isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (message.isRead) "✓✓" else "✓",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (message.isRead) Color(0xFF4FC3F7) else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
