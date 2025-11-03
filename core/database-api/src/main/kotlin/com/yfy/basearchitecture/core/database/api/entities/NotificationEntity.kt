package com.yfy.basearchitecture.core.database.api.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yfy.basearchitecture.core.database.api.BaseEntity

/**
 * Entity representing a notification in the database.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override var id: Long = 0L,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "message")
    val message: String,
    
    @ColumnInfo(name = "channel_id")
    val channelId: String,
    
    @ColumnInfo(name = "priority")
    val priority: String,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "deeplink")
    val deeplink: String?,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    
    @ColumnInfo(name = "is_read")
    val isRead: Boolean,
    
    @ColumnInfo(name = "is_scheduled")
    val isScheduled: Boolean,
    
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long?,
    
    @ColumnInfo(name = "metadata")
    val metadata: String, // JSON string
    
    @ColumnInfo(name = "created_at")
    override var createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    override var updatedAt: Long = System.currentTimeMillis()
) : BaseEntity(id, createdAt, updatedAt) 