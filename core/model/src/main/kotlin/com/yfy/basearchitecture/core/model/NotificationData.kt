package com.yfy.basearchitecture.core.model

/**
 * Data class representing a notification.
 */
data class NotificationData(
    val id: Int,
    val title: String,
    val message: String,
    val channelId: String,
    val priority: NotificationPriority = NotificationPriority.DEFAULT,
    val category: NotificationCategory = NotificationCategory.DEFAULT,
    val deeplink: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isScheduled: Boolean = false,
    val scheduledTime: Long? = null,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Enum representing notification priority levels.
 */
enum class NotificationPriority {
    MIN, LOW, DEFAULT, HIGH, MAX
}

/**
 * Enum representing notification categories.
 */
enum class NotificationCategory {
    DEFAULT, MESSAGE, PROMO, REMINDER, ALARM, EVENT, NEWS, SOCIAL
} 