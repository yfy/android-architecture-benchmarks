package com.yfy.basearchitecture.core.network.impl

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor() : TokenManager {
    
    private var accessToken: String? = null
    
    override fun getAccessToken(): String? = accessToken
    
    override fun setAccessToken(token: String) {
        accessToken = token
    }
    
    override fun clearToken() {
        accessToken = null
    }
}
