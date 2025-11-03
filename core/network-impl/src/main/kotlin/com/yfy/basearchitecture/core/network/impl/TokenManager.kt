package com.yfy.basearchitecture.core.network.impl

interface TokenManager {
    fun getAccessToken(): String?
    fun setAccessToken(token: String)
    fun clearToken()
} 