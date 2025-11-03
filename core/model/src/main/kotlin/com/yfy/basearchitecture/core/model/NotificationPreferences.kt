package com.yfy.basearchitecture.core.model

import kotlinx.serialization.Serializable

/**
 * Data class representing notification preferences.
 */
@Serializable
data class NotificationPreferences(
    val isEnabled: Boolean = true,
    val quietHours: QuietHours = QuietHours(),
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val priority: String = "default"
)

/**
 * Data class representing quiet hours settings.
 */
@Serializable
data class QuietHours(
    val isEnabled: Boolean = false,
    val startHour: Int = 22,
    val startMinute: Int = 0,
    val endHour: Int = 8,
    val endMinute: Int = 0
)

/**
 * Data class representing notification channel configuration.
 */
@Serializable
data class NotificationChannel(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val lightEnabled: Boolean = false
) 