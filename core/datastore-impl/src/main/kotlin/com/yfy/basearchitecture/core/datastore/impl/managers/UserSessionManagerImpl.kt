package com.yfy.basearchitecture.core.datastore.impl.managers

import com.yfy.basearchitecture.core.datastore.api.PreferenceKeys
import com.yfy.basearchitecture.core.datastore.api.interfaces.PreferenceManager
import com.yfy.basearchitecture.core.datastore.api.interfaces.UserSessionManager
import com.yfy.basearchitecture.core.datastore.api.models.UserSession
import com.yfy.basearchitecture.core.datastore.impl.utils.SerializationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.serializer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManagerImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : UserSessionManager {

    override suspend fun saveUserSession(session: UserSession) {
        preferenceManager.setObject(
            PreferenceKeys.USER_SESSION,
            session,
            serializer()
        )
    }

    override fun getUserSession(): Flow<UserSession?> {
        return preferenceManager.getString(PreferenceKeys.USER_SESSION, "")
            .map { jsonString ->
                when {
                    jsonString.isBlank() -> null
                    else -> {
                        try {
                            val session = SerializationUtils.deserialize(
                                jsonString,
                                serializer<UserSession>()
                            )
                            if (session.expiresAt < System.currentTimeMillis()) { null } else { session }
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to deserialize user session")
                            null
                        }
                    }
                }
            }
    }

    override suspend fun clearUserSession() {
        preferenceManager.remove(PreferenceKeys.USER_SESSION)
        preferenceManager.remove(PreferenceKeys.ACCESS_TOKEN)
        preferenceManager.remove(PreferenceKeys.REFRESH_TOKEN)
        preferenceManager.remove(PreferenceKeys.USER_ID)
        preferenceManager.remove(PreferenceKeys.USERNAME)
    }

    override suspend fun saveAccessToken(token: String) {
        preferenceManager.setString(PreferenceKeys.ACCESS_TOKEN, token)
    }

    override fun getAccessToken(): Flow<String?> {
        return preferenceManager.getString(PreferenceKeys.ACCESS_TOKEN, "").map { token ->
            token.ifBlank { null }
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        preferenceManager.setString(PreferenceKeys.REFRESH_TOKEN, token)
    }

    override fun getRefreshToken(): Flow<String?> {
        return preferenceManager.getString(PreferenceKeys.REFRESH_TOKEN, "").map { token ->
            token.ifBlank { null }
        }
    }

    override suspend fun saveUserId(userId: String) {
        preferenceManager.setString(PreferenceKeys.USER_ID, userId)
    }

    override fun getUserId(): Flow<String?> {
        return preferenceManager.getString(PreferenceKeys.USER_ID, "").map { userId ->
            userId.ifBlank { null }
        }
    }

    override suspend fun saveUsername(username: String) {
        preferenceManager.setString(PreferenceKeys.USERNAME, username)
    }

    override fun getUsername(): Flow<String?> {
        return preferenceManager.getString(PreferenceKeys.USERNAME, "").map { username ->
            username.ifBlank { null }
        }
    }

    override suspend fun isSessionValid(): Boolean {
        return try {
            var isValid = false
            getUserSession().collect { session ->
                isValid = session != null && session.expiresAt > System.currentTimeMillis()
            }
            isValid
        } catch (e: Exception) {
            false
        }
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return getUserSession().map { session ->
            session != null && session.expiresAt > System.currentTimeMillis()
        }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        preferenceManager.setBoolean(PreferenceKeys.BIOMETRIC_ENABLED, enabled)
    }

    override fun isBiometricEnabled(): Flow<Boolean> {
        return preferenceManager.getBoolean(PreferenceKeys.BIOMETRIC_ENABLED, false)
    }

    override suspend fun setAutoLoginEnabled(enabled: Boolean) {
        preferenceManager.setBoolean(PreferenceKeys.AUTO_LOGIN_ENABLED, enabled)
    }

    override fun isAutoLoginEnabled(): Flow<Boolean> {
        return preferenceManager.getBoolean(PreferenceKeys.AUTO_LOGIN_ENABLED, false)
    }
}