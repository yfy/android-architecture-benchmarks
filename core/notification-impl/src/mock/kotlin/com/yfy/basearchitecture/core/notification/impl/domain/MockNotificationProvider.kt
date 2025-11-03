package com.yfy.basearchitecture.core.notification.impl.domain

import com.yfy.basearchitecture.core.model.NotificationChannel
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.model.NotificationPreferences
import com.yfy.basearchitecture.core.notification.api.domain.NotificationProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

import javax.inject.Inject

class MockNotificationProvider @Inject constructor() : NotificationProvider {
    
    private val _notifications = MutableStateFlow<List<NotificationData>>(emptyList())
    val notifications: StateFlow<List<NotificationData>> = _notifications
    
    private val _preferences = MutableStateFlow(NotificationPreferences())
    private val _unreadCount = MutableStateFlow(0)
    
    override suspend fun showNotification(notification: NotificationData) {
        Timber.d("Mock: Showing notification - ${notification.title}")
        val currentList = _notifications.value.toMutableList()
        currentList.add(notification)
        _notifications.value = currentList
        _unreadCount.value = _unreadCount.value + 1
    }
    
    override suspend fun scheduleNotification(notification: NotificationData, delay: Long) {
        Timber.d("Mock: Scheduling notification - ${notification.title} in ${delay}ms")
        // In mock, we just add it immediately
        showNotification(notification)
    }
    
    override suspend fun cancelNotification(id: Int) {
        Timber.d("Mock: Cancelling notification - $id")
        val currentList = _notifications.value.toMutableList()
        currentList.removeAll { it.id == id }
        _notifications.value = currentList
    }
    
    override suspend fun cancelAllNotifications() {
        Timber.d("Mock: Cancelling all notifications")
        _notifications.value = emptyList()
        _unreadCount.value = 0
    }
    
    override suspend fun createNotificationChannel(channel: NotificationChannel) {
        Timber.d("Mock: Creating notification channel - ${channel.name}")
    }
    
    override suspend fun deleteNotificationChannel(channelId: String) {
        Timber.d("Mock: Deleting notification channel - $channelId")
    }
    
    override suspend fun isNotificationPermissionGranted(): Boolean {
        return true // Mock always returns true
    }
    
    override suspend fun requestNotificationPermission() {
        Timber.d("Mock: Requesting notification permission")
    }
    

    override suspend fun areNotificationsEnabled(): Boolean = true

    override fun getUnreadCount(): Flow<Int> = _unreadCount
    
    override suspend fun markAsRead(notificationId: Int) {
        val currentList = _notifications.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _notifications.value = currentList
            _unreadCount.value = _unreadCount.value - 1
        }
    }
    
    override suspend fun markAllAsRead() {
        val currentList = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = currentList
        _unreadCount.value = 0
    }
} 