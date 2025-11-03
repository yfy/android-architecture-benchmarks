package com.yfy.basearchitecture.core.notification.impl.utils

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import com.yfy.basearchitecture.core.model.NotificationCategory
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.model.NotificationPreferences
import com.yfy.basearchitecture.core.model.NotificationPriority
import com.yfy.basearchitecture.core.notification.impl.data.remote.FcmService

fun NotificationData.toNotificationCompatBuilder(
    context: Context,
    preferences: NotificationPreferences
): NotificationCompat.Builder {
    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // You should use your app's icon
        .setPriority(getPriority())
        .setAutoCancel(true)
        .setCategory(getCategory())
    
    // Set sound, vibration, and LED based on preferences
    if (preferences.soundEnabled) {
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND)
    }
    
    if (preferences.vibrationEnabled) {
        builder.setVibrate(longArrayOf(0, 250, 250, 250))
    }
    
    // Handle deeplink
    deeplink?.let { link ->
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = link.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = FcmService.createPendingIntent(
            context = context,
            intent = intent,
            requestCode = id
        )
        
        builder.setContentIntent(pendingIntent)
    }
    
    if (deeplink == null) {
        val dismissIntent = Intent("NOTIFICATION_DISMISS").apply {
            putExtra("notification_id", id)
        }
        
        val dismissPendingIntent = FcmService.createPendingIntent(
            context = context,
            intent = dismissIntent,
            requestCode = id * 2
        )
        
        builder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            getString(context,android.R.string.ok),
            dismissPendingIntent
        )
    }
    
    return builder
}

private fun NotificationData.getPriority(): Int {
    return when (this.priority) {
        NotificationPriority.MIN -> NotificationCompat.PRIORITY_MIN
        NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
        NotificationPriority.DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
        NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
        NotificationPriority.MAX -> NotificationCompat.PRIORITY_MAX
    }
}

private fun NotificationData.getCategory(): String {
    return when (category) {
        NotificationCategory.MESSAGE -> NotificationCompat.CATEGORY_MESSAGE
        NotificationCategory.PROMO -> NotificationCompat.CATEGORY_PROMO
        NotificationCategory.REMINDER -> NotificationCompat.CATEGORY_REMINDER
        NotificationCategory.ALARM -> NotificationCompat.CATEGORY_ALARM
        NotificationCategory.EVENT -> NotificationCompat.CATEGORY_EVENT
        NotificationCategory.NEWS -> NotificationCompat.CATEGORY_SOCIAL
        NotificationCategory.SOCIAL -> NotificationCompat.CATEGORY_SOCIAL
        NotificationCategory.DEFAULT -> NotificationCompat.CATEGORY_SYSTEM
    }
} 