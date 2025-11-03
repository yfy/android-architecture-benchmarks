package com.yfy.basearchitecture.core.database.api.dao

import com.yfy.basearchitecture.core.database.api.BaseDao
import com.yfy.basearchitecture.core.database.api.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for notification database operations.
 * Provides methods for managing notification data.
 */
interface NotificationDao : BaseDao<NotificationEntity> {
    
    /**
     * Get all notifications ordered by timestamp descending
     */
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    
    /**
     * Get notification by ID
     */
    suspend fun getNotificationById(id: Int): NotificationEntity?
    
    /**
     * Get count of unread notifications
     */
    fun getUnreadCount(): Flow<Int>
    
    /**
     * Insert a new notification
     */
    suspend fun insertNotification(notification: NotificationEntity): Long
    
    /**
     * Update an existing notification
     */
    suspend fun updateNotification(notification: NotificationEntity)
    
    /**
     * Mark notification as read by ID
     */
    suspend fun markAsRead(id: Int)
    
    /**
     * Mark all notifications as read
     */
    suspend fun markAllAsRead()
    
    /**
     * Delete a specific notification
     */
    suspend fun deleteNotification(notification: NotificationEntity)
    
    /**
     * Delete notification by ID
     */
    suspend fun deleteNotificationById(id: Int)
    
    /**
     * Delete all notifications
     */
    suspend fun deleteAllNotifications()
    
    /**
     * Delete old notifications before specified timestamp
     */
    suspend fun deleteOldNotifications(timestamp: Long)
} 