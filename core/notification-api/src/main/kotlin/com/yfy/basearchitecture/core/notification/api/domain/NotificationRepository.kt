package com.yfy.basearchitecture.core.notification.api.domain

import com.yfy.basearchitecture.core.model.NotificationData
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationData>>
    suspend fun getNotificationById(id: Int): NotificationData?
    fun getUnreadCount(): Flow<Int>
    suspend fun insertNotification(notification: NotificationData): Long
    suspend fun updateNotification(notification: NotificationData)
    suspend fun markAsRead(id: Int)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notification: NotificationData)
    suspend fun deleteNotificationById(id: Int)
    suspend fun deleteAllNotifications()
    suspend fun deleteOldNotifications(timestamp: Long)
}