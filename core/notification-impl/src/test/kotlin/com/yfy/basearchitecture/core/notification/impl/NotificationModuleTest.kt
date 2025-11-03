package com.yfy.basearchitecture.core.notification.impl

import com.yfy.basearchitecture.core.model.NotificationCategory
import com.yfy.basearchitecture.core.model.NotificationData
import com.yfy.basearchitecture.core.model.NotificationPriority
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationModuleTest {
    
    @Test
    fun testNotificationDataCreation() {
        val notification = NotificationData(
            id = 1,
            title = "Test Notification",
            message = "This is a test notification",
            channelId = "test_channel",
            priority = NotificationPriority.DEFAULT,
            category = NotificationCategory.DEFAULT
        )
        
        assertEquals(1, notification.id)
        assertEquals("Test Notification", notification.title)
        assertEquals("This is a test notification", notification.message)
        assertEquals("test_channel", notification.channelId)
        assertEquals(NotificationPriority.DEFAULT, notification.priority)
        assertEquals(NotificationCategory.DEFAULT, notification.category)
    }
    
    @Test
    fun testNotificationDataWithDeeplink() {
        val notification = NotificationData(
            id = 2,
            title = "Deeplink Test",
            message = "Click to open",
            channelId = "deeplink_channel",
            deeplink = "myapp://test"
        )
        
        assertEquals("myapp://test", notification.deeplink)
    }
} 