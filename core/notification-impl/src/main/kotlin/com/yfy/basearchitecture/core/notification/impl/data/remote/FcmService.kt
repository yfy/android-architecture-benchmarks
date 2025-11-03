package com.yfy.basearchitecture.core.notification.impl.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.model.NotificationPriority
import com.yfy.basearchitecture.core.notification.impl.domain.NotificationProviderImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationProvider: NotificationProviderImpl
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val notificationData = remoteMessage.toNotificationData()
        
        CoroutineScope(Dispatchers.Main).launch {
            notificationProvider.showNotification(notificationData)
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle new FCM token
        // You can send this token to your server
    }
    
    private fun RemoteMessage.toNotificationData(): NotificationData {
        val data = data
        return NotificationData(
            id = data["id"]?.toIntOrNull() ?: System.currentTimeMillis().toInt(),
            title = data["title"] ?: notification?.title ?: "New Message",
            message = data["message"] ?: notification?.body ?: "",
            channelId = data["channel_id"] ?: "default",
            priority = data["priority"]?.let { 
                try { NotificationPriority.valueOf(it.uppercase()) }
                catch (e: IllegalArgumentException) { NotificationPriority.DEFAULT }
            } ?: NotificationPriority.DEFAULT,
            deeplink = data["deeplink"],
            imageUrl = data["image_url"],
            metadata = data
        )
    }
    
    companion object {
        fun createNotificationChannel(
            context: Context,
            channelId: String,
            channelName: String,
            description: String? = null,
            importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    this.description = description
                }
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
        
        fun createPendingIntent(
            context: Context,
            intent: Intent,
            requestCode: Int,
            flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        ): PendingIntent {
            return PendingIntent.getActivity(context, requestCode, intent, flags)
        }
    }
} 