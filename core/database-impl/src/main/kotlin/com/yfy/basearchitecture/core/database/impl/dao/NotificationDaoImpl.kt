package com.yfy.basearchitecture.core.database.impl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yfy.basearchitecture.core.database.api.dao.NotificationDao
import com.yfy.basearchitecture.core.database.api.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of NotificationDao interface using Room.
 */
@Dao
interface NotificationDaoImpl : NotificationDao {
    
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    override fun getAllNotifications(): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    override suspend fun getNotificationById(id: Int): NotificationEntity?
    
    @Query("SELECT COUNT(*) FROM notifications WHERE is_read = 0")
    override fun getUnreadCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertNotification(notification: NotificationEntity): Long
    
    @Update
    override suspend fun updateNotification(notification: NotificationEntity)
    
    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id")
    override suspend fun markAsRead(id: Int)
    
    @Query("UPDATE notifications SET is_read = 1")
    override suspend fun markAllAsRead()
    
    @Delete
    override suspend fun deleteNotification(notification: NotificationEntity)
    
    @Query("DELETE FROM notifications WHERE id = :id")
    override suspend fun deleteNotificationById(id: Int)
    
    @Query("DELETE FROM notifications")
    override suspend fun deleteAllNotifications()
    
    @Query("DELETE FROM notifications WHERE timestamp < :timestamp")
    override suspend fun deleteOldNotifications(timestamp: Long)
    
    // BaseDao implementations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(entity: NotificationEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(entities: List<NotificationEntity>): List<Long>
    
    @Update
    override suspend fun update(entity: NotificationEntity)
    
    @Update
    override suspend fun updateAll(entities: List<NotificationEntity>)
    
    @Delete
    override suspend fun delete(entity: NotificationEntity)
    
    @Delete
    override suspend fun deleteAll(entities: List<NotificationEntity>)
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    override suspend fun getById(id: Long): NotificationEntity?
    
    @Query("SELECT * FROM notifications")
    override suspend fun getAll(): List<NotificationEntity>
    
    @Query("SELECT * FROM notifications")
    override fun getAllAsFlow(): Flow<List<NotificationEntity>>
    
    @Query("SELECT COUNT(*) FROM notifications")
    override suspend fun count(): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM notifications WHERE id = :id)")
    override suspend fun exists(id: Long): Boolean
    
    @Query("DELETE FROM notifications WHERE id = :id")
    override suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM notifications")
    override suspend fun deleteAll()
} 