package com.yfy.basearchitecture.core.database.api.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yfy.basearchitecture.core.database.api.BaseEntity

/**
 * Entity representing a user in the database.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override var id: Long = 0L,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "first_name")
    val firstName: String,
    
    @ColumnInfo(name = "last_name")
    val lastName: String,
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean = false,
    
    @ColumnInfo(name = "last_login_at")
    val lastLoginAt: Long? = null,
    
    @ColumnInfo(name = "created_at")
    override var createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    override var updatedAt: Long = System.currentTimeMillis()
) : BaseEntity(id, createdAt, updatedAt) 