package com.yfy.basearchitecture.core.notification.impl.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.notification.api.domain.NotificationProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class ScheduledNotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val notificationProvider: NotificationProvider
) : CoroutineWorker(appContext, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val notificationId = inputData.getInt("notification_id", 0)
            val title = inputData.getString("title") ?: "Scheduled Notification"
            val message = inputData.getString("message") ?: ""
            val channelId = inputData.getString("channel_id") ?: "default"
            val deeplink = inputData.getString("deeplink")
            
            val notificationData = NotificationData(
                id = notificationId,
                title = title,
                message = message,
                channelId = channelId,
                deeplink = deeplink
            )
            
            notificationProvider.showNotification(notificationData)
            
            Timber.d("Scheduled notification shown: $title")
            Result.success()
            
        } catch (e: Exception) {
            Timber.e(e, "Error in scheduled notification worker")
            Result.failure()
        }
    }
} 