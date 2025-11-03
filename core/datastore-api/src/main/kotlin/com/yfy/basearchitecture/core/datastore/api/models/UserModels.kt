package com.yfy.basearchitecture.core.datastore.api.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userId: String = "",
    val username: String = "",
    val email: String? = null,
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresAt: Long = 0L,
    val lastLoginTime: Long = System.currentTimeMillis(),
    val deviceId: String? = null
)