package com.yfy.basearchitecture.core.database.api

import com.yfy.basearchitecture.core.database.api.entities.NotificationEntity
import com.yfy.basearchitecture.core.database.api.entities.UserEntity
import org.junit.Test
import org.junit.Assert.*

class DatabaseApiTest {
    
    @Test
    fun testBaseEntityCreation() {
        val entity = object : BaseEntity() {}
        assertNotNull(entity)
        assertEquals(0L, entity.id)
        assertTrue(entity.createdAt > 0)
        assertTrue(entity.updatedAt > 0)
    }
    
    @Test
    fun testUserEntityCreation() {
        val user = UserEntity(
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User"
        )
        
        assertEquals("testuser", user.username)
        assertEquals("test@example.com", user.email)
        assertEquals("Test", user.firstName)
        assertEquals("User", user.lastName)
        assertFalse(user.isVerified)
        assertNull(user.avatarUrl)
        assertNull(user.lastLoginAt)
    }
    
    @Test
    fun testNotificationEntityCreation() {
        val notification = NotificationEntity(
            title = "Test Notification",
            message = "This is a test notification",
            channelId = "test_channel",
            priority = "high",
            category = "test",
            deeplink = null,
            imageUrl = null,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            isScheduled = false,
            scheduledTime = null,
            metadata = "{}"
        )
        
        assertEquals("Test Notification", notification.title)
        assertEquals("This is a test notification", notification.message)
        assertEquals("test_channel", notification.channelId)
        assertEquals("high", notification.priority)
        assertEquals("test", notification.category)
        assertFalse(notification.isRead)
        assertFalse(notification.isScheduled)
        assertNull(notification.deeplink)
        assertNull(notification.imageUrl)
        assertNull(notification.scheduledTime)
    }
} 