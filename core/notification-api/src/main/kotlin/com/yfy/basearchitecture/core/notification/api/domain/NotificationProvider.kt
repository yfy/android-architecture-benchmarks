package com.yfy.basearchitecture.core.notification.api.domain

import com.yfy.basearchitecture.core.model.NotificationChannel
import com.yfy.basearchitecture.core.model.NotificationData
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing notifications in the application.
 * Provides methods for showing, scheduling, and managing notification preferences.
 */
interface NotificationProvider {
    
    /**
     * Show an immediate notification
     */
    suspend fun showNotification(notification: NotificationData)
    
    /**
     * Schedule a notification for later
     */
    suspend fun scheduleNotification(notification: NotificationData, delay: Long)
    
    /**
     * Cancel a scheduled notification
     */
    suspend fun cancelNotification(id: Int)
    
    /**
     * Cancel all notifications
     */
    suspend fun cancelAllNotifications()
    
    /**
     * Create a notification channel
     */
    suspend fun createNotificationChannel(channel: NotificationChannel)
    
    /**
     * Delete a notification channel
     */
    suspend fun deleteNotificationChannel(channelId: String)
    
    /**
     * Check if notification permissions are granted
     */
    suspend fun isNotificationPermissionGranted(): Boolean
    
    /**
     * Request notification permissions
     */
    suspend fun requestNotificationPermission()

    /**
     * Check if notifications are enabled
     */
    suspend fun areNotificationsEnabled(): Boolean

    /**
     * Get unread notification count
     */
    fun getUnreadCount(): Flow<Int>
    
    /**
     * Mark notification as read
     */
    suspend fun markAsRead(notificationId: Int)
    
    /**
     * Mark all notifications as read
     */
    suspend fun markAllAsRead()
} 