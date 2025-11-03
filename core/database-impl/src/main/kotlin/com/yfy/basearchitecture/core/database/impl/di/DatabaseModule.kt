package com.yfy.basearchitecture.core.database.impl.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yfy.basearchitecture.core.database.api.DatabaseProvider
import com.yfy.basearchitecture.core.database.api.dao.NotificationDao
import com.yfy.basearchitecture.core.database.api.dao.UserDao
import com.yfy.basearchitecture.core.database.impl.AppDatabase
import com.yfy.basearchitecture.core.database.impl.DatabaseProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for database dependencies.
 * Provides database instance and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
        .fallbackToDestructiveMigration(false)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()
    }
    
    @Provides
    @Singleton
    fun provideDatabaseProvider(
        databaseProviderImpl: DatabaseProviderImpl
    ): DatabaseProvider {
        return databaseProviderImpl
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }
}