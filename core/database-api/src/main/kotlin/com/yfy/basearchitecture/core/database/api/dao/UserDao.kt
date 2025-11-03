package com.yfy.basearchitecture.core.database.api.dao

import com.yfy.basearchitecture.core.database.api.BaseDao
import com.yfy.basearchitecture.core.database.api.entities.UserEntity

/**
 * Interface for user database operations.
 * Provides methods for managing user data.
 */
interface UserDao : BaseDao<UserEntity> {
    
    /**
     * Get user by email
     */
    suspend fun getByEmail(email: String): UserEntity?
    
    /**
     * Get user by username
     */
    suspend fun getByUsername(username: String): UserEntity?
    
    /**
     * Get users by verification status
     */
    suspend fun getByVerificationStatus(isVerified: Boolean): List<UserEntity>
    
    /**
     * Update last login timestamp for user
     */
    suspend fun updateLastLogin(userId: String, timestamp: Long)
} 