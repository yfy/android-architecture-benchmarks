package com.yfy.basearchitecture.core.notification.impl.data.repository

import com.yfy.basearchitecture.core.database.api.dao.NotificationDao
import com.yfy.basearchitecture.core.database.api.entities.NotificationEntity
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.notification.api.domain.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationRepository using Room database.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    
    override fun getAllNotifications(): Flow<List<NotificationData>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { it.toNotificationData() }
        }
    }
    
    override suspend fun getNotificationById(id: Int): NotificationData? {
        return notificationDao.getNotificationById(id)?.toNotificationData()
    }
    
    override fun getUnreadCount(): Flow<Int> {
        return notificationDao.getUnreadCount()
    }
    
    override suspend fun insertNotification(notification: NotificationData): Long {
        return notificationDao.insertNotification(notification.toNotificationEntity())
    }
    
    override suspend fun updateNotification(notification: NotificationData) {
        notificationDao.updateNotification(notification.toNotificationEntity())
    }
    
    override suspend fun markAsRead(id: Int) {
        notificationDao.markAsRead(id)
    }
    
    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }
    
    override suspend fun deleteNotification(notification: NotificationData) {
        notificationDao.deleteNotification(notification.toNotificationEntity())
    }
    
    override suspend fun deleteNotificationById(id: Int) {
        notificationDao.deleteNotificationById(id)
    }
    
    override suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }
    
    override suspend fun deleteOldNotifications(timestamp: Long) {
        notificationDao.deleteOldNotifications(timestamp)
    }

    // Extension functions for conversion
    private fun NotificationEntity.toNotificationData(): NotificationData {
        return NotificationData(
            id = this.id.toInt(),
            title = this.title,
            message = this.message,
            channelId = this.channelId,
            priority = com.yfy.basearchitecture.core.model.NotificationPriority.valueOf(this.priority.uppercase()),
            category = com.yfy.basearchitecture.core.model.NotificationCategory.valueOf(this.category.uppercase()),
            deeplink = this.deeplink,
            imageUrl = this.imageUrl,
            timestamp = this.timestamp,
            isRead = this.isRead,
            isScheduled = this.isScheduled,
            scheduledTime = this.scheduledTime,
            metadata = this.metadata.split(",").associate { 
                val (key, value) = it.split("=", limit = 2)
                key to value
            }
        )
    }
    
    private fun NotificationData.toNotificationEntity(): NotificationEntity {
        return NotificationEntity(
            id = this.id.toLong(),
            title = this.title,
            message = this.message,
            channelId = this.channelId,
            priority = this.priority.name.lowercase(),
            category = this.category.name.lowercase(),
            deeplink = this.deeplink,
            imageUrl = this.imageUrl,
            timestamp = this.timestamp,
            isRead = this.isRead,
            isScheduled = this.isScheduled,
            scheduledTime = this.scheduledTime,
            metadata = this.metadata.entries.joinToString(",") { "${it.key}=${it.value}" }
        )
    }
} 