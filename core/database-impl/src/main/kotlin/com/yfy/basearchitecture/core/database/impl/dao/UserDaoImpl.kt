package com.yfy.basearchitecture.core.database.impl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yfy.basearchitecture.core.database.api.dao.UserDao
import com.yfy.basearchitecture.core.database.api.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of UserDao interface using Room.
 */
@Dao
interface UserDaoImpl : UserDao {
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    override suspend fun getByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    override suspend fun getByUsername(username: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE is_verified = :isVerified")
    override suspend fun getByVerificationStatus(isVerified: Boolean): List<UserEntity>
    
    @Query("UPDATE users SET last_login_at = :timestamp WHERE id = :userId")
    override suspend fun updateLastLogin(userId: String, timestamp: Long)
    
    // BaseDao implementations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(entity: UserEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(entities: List<UserEntity>): List<Long>

    @Update
    override suspend fun update(entity: UserEntity)
    
    @Update
    override suspend fun updateAll(entities: List<UserEntity>)
    
    @Delete
    override suspend fun delete(entity: UserEntity)
    
    @Delete
    override suspend fun deleteAll(entities: List<UserEntity>)
    
    @Query("SELECT * FROM users WHERE id = :id")
    override suspend fun getById(id: Long): UserEntity?
    
    @Query("SELECT * FROM users")
    override suspend fun getAll(): List<UserEntity>
    
    @Query("SELECT * FROM users")
    override fun getAllAsFlow(): Flow<List<UserEntity>>
    
    @Query("SELECT COUNT(*) FROM users")
    override suspend fun count(): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :id)")
    override suspend fun exists(id: Long): Boolean
    
    @Query("DELETE FROM users WHERE id = :id")
    override suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM users")
    override suspend fun deleteAll()
} 