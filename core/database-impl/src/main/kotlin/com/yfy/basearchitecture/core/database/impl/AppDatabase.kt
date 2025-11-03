package com.yfy.basearchitecture.core.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yfy.basearchitecture.core.database.api.entities.NotificationEntity
import com.yfy.basearchitecture.core.database.api.entities.UserEntity
import com.yfy.basearchitecture.core.database.impl.converters.DateConverters
import com.yfy.basearchitecture.core.database.impl.dao.NotificationDaoImpl
import com.yfy.basearchitecture.core.database.impl.dao.UserDaoImpl

/**
 * Main database class for the application.
 * Provides access to all DAOs and manages database lifecycle.
 */
@Database(
    entities = [
        UserEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDaoImpl
    abstract fun notificationDao(): NotificationDaoImpl
}