package com.yfy.basearchitecture.core.notification.impl.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.datastore.api.interfaces.AppSettingsManager
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.notification.api.domain.NotificationProvider
import com.yfy.basearchitecture.core.notification.api.domain.NotificationRepository
import com.yfy.basearchitecture.core.notification.impl.domain.workers.ScheduledNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import com.yfy.basearchitecture.core.model.NotificationChannel as ModelNotificationChannel

/**
 * Implementation of NotificationProvider using Android NotificationManager.
 */
@Singleton
class NotificationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val appSettingsManager: AppSettingsManager,
    private val analyticsProvider: AnalyticsProvider
) : NotificationProvider {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val workManager = WorkManager.getInstance(context)
    
    override suspend fun showNotification(notification: NotificationData) {
        if (!shouldShowNotification(notification)) return
        
        // Save to database
        val notificationId = notificationRepository.insertNotification(notification)
        
        // Create notification channel if needed
        createNotificationChannelIfNeeded(notification.channelId)
        
        // Build and show notification
        val builder = NotificationCompat.Builder(context, notification.channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(getNotificationPriority(notification.priority))
            .setAutoCancel(true)
            .setContentIntent(createPendingIntent(notification))
        
        // Add image if available
        notification.imageUrl?.let { url ->
            // Load and set image (implementation depends on image loading library)
        }
        
        // Show notification
        notificationManager.notify(notification.id, builder.build())
        
        // Track analytics
        analyticsProvider.logEvent("notification_shown", mapOf(
            "notification_id" to notification.id.toString(),
            "channel_id" to notification.channelId,
            "priority" to notification.priority.name,
            "category" to notification.category.name
        ))
    }
    
    override suspend fun scheduleNotification(notification: NotificationData, delay: Long) {
        // Save to database with scheduled flag
        val scheduledNotification = notification.copy(
            isScheduled = true,
            scheduledTime = System.currentTimeMillis() + delay
        )
        val notificationId = notificationRepository.insertNotification(scheduledNotification)
        
        // Schedule with WorkManager
        val inputData = Data.Builder()
            .putInt("notification_id", notificationId.toInt())
            .putString("title", notification.title)
            .putString("message", notification.message)
            .putString("channel_id", notification.channelId)
            .putString("deeplink", notification.deeplink)
            .build()
            
        val workRequest = OneTimeWorkRequestBuilder<ScheduledNotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()
        
        workManager.enqueue(workRequest)
        
        // Track analytics
        analyticsProvider.logEvent("notification_scheduled", mapOf(
            "notification_id" to notification.id.toString(),
            "delay_millis" to delay.toString()
        ))
    }
    
    override suspend fun cancelNotification(id: Int) {
        // Cancel from notification manager
        notificationManager.cancel(id)
        
        // Cancel from WorkManager
        workManager.cancelAllWorkByTag("notification_$id")
        
        // Delete from database
        notificationRepository.deleteNotificationById(id)
        
        // Track analytics
        analyticsProvider.logEvent("notification_cancelled", mapOf(
            "notification_id" to id.toString()
        ))
    }
    
    override suspend fun cancelAllNotifications() {
        // Cancel all notifications
        notificationManager.cancelAll()
        
        // Cancel all scheduled work
        workManager.cancelAllWork()
        
        // Delete all from database
        notificationRepository.deleteAllNotifications()
        
        // Track analytics
        analyticsProvider.logEvent("all_notifications_cancelled")
    }
    
    override suspend fun createNotificationChannel(channel: ModelNotificationChannel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channel.id,
                channel.name,
                channel.importance
            ).apply {
                description = channel.description
                enableLights(channel.lightEnabled)
                enableVibration(channel.vibrationEnabled)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    
    override suspend fun deleteNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }
    
    override suspend fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            // For older versions, we assume notifications are enabled
            // as there's no reliable way to check on older Android versions
            true
        }
    }
    
    override suspend fun requestNotificationPermission() {
        // This would typically be handled by the UI layer
        // For now, we'll just track the request
        analyticsProvider.logEvent("notification_permissions_requested")
    }
    

    override suspend fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            // For older versions, we assume notifications are enabled
            // as there's no reliable way to check on older Android versions
            true
        }
    }
    

    override fun getUnreadCount(): Flow<Int> {
        return notificationRepository.getUnreadCount()
    }
    
    override suspend fun markAsRead(notificationId: Int) {
        notificationRepository.markAsRead(notificationId)
        
        // Track analytics
        analyticsProvider.logEvent("notification_marked_read", mapOf(
            "notification_id" to notificationId.toString()
        ))
    }
    
    override suspend fun markAllAsRead() {
        notificationRepository.markAllAsRead()
        
        // Track analytics
        analyticsProvider.logEvent("all_notifications_marked_read")
    }
    
    private suspend fun shouldShowNotification(notification: NotificationData): Boolean {
        val preferences = appSettingsManager.getNotificationSettings().first()
        
        // Check if notifications are enabled
        if (!preferences.inAppEnabled) return false
        
        // Check category preferences
        val categoryEnabled = when (notification.category) {
            com.yfy.basearchitecture.core.model.NotificationCategory.DEFAULT -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.MESSAGE -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.PROMO -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.REMINDER -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.ALARM -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.EVENT -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.NEWS -> true
            com.yfy.basearchitecture.core.model.NotificationCategory.SOCIAL -> true
        }
        
        return categoryEnabled
    }
    
    private suspend fun createNotificationChannelIfNeeded(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = ModelNotificationChannel(
                    id = channelId,
                    name = "Default Channel",
                    description = "Default notification channel",
                    importance = NotificationManager.IMPORTANCE_DEFAULT
                )
                createNotificationChannel(channel)
            }
        }
    }
    
    private fun getNotificationPriority(priority: com.yfy.basearchitecture.core.model.NotificationPriority): Int {
        return when (priority) {
            com.yfy.basearchitecture.core.model.NotificationPriority.MIN -> NotificationCompat.PRIORITY_MIN
            com.yfy.basearchitecture.core.model.NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            com.yfy.basearchitecture.core.model.NotificationPriority.DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
            com.yfy.basearchitecture.core.model.NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
            com.yfy.basearchitecture.core.model.NotificationPriority.MAX -> NotificationCompat.PRIORITY_MAX
        }
    }
    
    private fun createPendingIntent(notification: NotificationData): android.app.PendingIntent? {
        // Implementation depends on your navigation/deeplink system
        // For now, return null
        return null
    }
} 