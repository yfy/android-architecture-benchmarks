package com.yfy.basearchitecture.core.datastore.api.interfaces

import com.yfy.basearchitecture.core.datastore.api.models.UserSession
import kotlinx.coroutines.flow.Flow

interface UserSessionManager {
    
    // Session management
    suspend fun saveUserSession(session: UserSession)
    fun getUserSession(): Flow<UserSession?>
    suspend fun clearUserSession()
    
    // Token management
    suspend fun saveAccessToken(token: String)
    fun getAccessToken(): Flow<String?>
    
    suspend fun saveRefreshToken(token: String)
    fun getRefreshToken(): Flow<String?>
    
    // User info
    suspend fun saveUserId(userId: String)
    fun getUserId(): Flow<String?>
    
    suspend fun saveUsername(username: String)
    fun getUsername(): Flow<String?>
    
    // Session validation
    suspend fun isSessionValid(): Boolean
    fun isUserLoggedIn(): Flow<Boolean>
    
    // Biometric settings
    suspend fun setBiometricEnabled(enabled: Boolean)
    fun isBiometricEnabled(): Flow<Boolean>
    
    // Auto login
    suspend fun setAutoLoginEnabled(enabled: Boolean)
    fun isAutoLoginEnabled(): Flow<Boolean>
}