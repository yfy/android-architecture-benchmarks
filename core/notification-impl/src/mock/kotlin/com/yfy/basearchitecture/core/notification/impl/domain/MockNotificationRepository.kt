package com.yfy.basearchitecture.core.notification.impl.domain

import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.model.NotificationPreferences
import com.yfy.basearchitecture.core.notification.api.domain.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

import javax.inject.Inject

class MockNotificationRepository @Inject constructor() : NotificationRepository {
    
    private val _notifications = MutableStateFlow<List<NotificationData>>(emptyList())
    private val _preferences = MutableStateFlow(NotificationPreferences())
    
    override fun getAllNotifications(): Flow<List<NotificationData>> {
        return _notifications
    }
    
    override suspend fun getNotificationById(id: Int): NotificationData? {
        return _notifications.value.find { it.id == id }
    }
    
    override fun getUnreadCount(): Flow<Int> {
        return MutableStateFlow(_notifications.value.count { !it.isRead })
    }
    
    override suspend fun insertNotification(notification: NotificationData): Long {
        val currentList = _notifications.value.toMutableList()
        currentList.add(notification)
        _notifications.value = currentList
        return notification.id.toLong()
    }
    
    override suspend fun updateNotification(notification: NotificationData) {
        val currentList = _notifications.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            currentList[index] = notification
            _notifications.value = currentList
        }
    }
    
    override suspend fun markAsRead(id: Int) {
        val currentList = _notifications.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(isRead = true)
            _notifications.value = currentList
        }
    }
    
    override suspend fun markAllAsRead() {
        val currentList = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = currentList
    }
    
    override suspend fun deleteNotification(notification: NotificationData) {
        val currentList = _notifications.value.toMutableList()
        currentList.removeAll { it.id == notification.id }
        _notifications.value = currentList
    }
    
    override suspend fun deleteNotificationById(id: Int) {
        val currentList = _notifications.value.toMutableList()
        currentList.removeAll { it.id == id }
        _notifications.value = currentList
    }
    
    override suspend fun deleteAllNotifications() {
        _notifications.value = emptyList()
    }
    
    override suspend fun deleteOldNotifications(timestamp: Long) {
        val currentList = _notifications.value.toMutableList()
        currentList.removeAll { it.timestamp < timestamp }
        _notifications.value = currentList
    }
    
}