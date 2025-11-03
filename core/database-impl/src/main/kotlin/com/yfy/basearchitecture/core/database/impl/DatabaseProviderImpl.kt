package com.yfy.basearchitecture.core.database.impl

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yfy.basearchitecture.core.database.api.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseProviderImpl @Inject constructor(
    private val context: Context
) : DatabaseProvider {

    private var database: AppDatabase? = null

    override fun getDatabase(): RoomDatabase {
        return database ?: createDatabase().also { database = it }
    }

    private fun createDatabase(): AppDatabase {
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "yfy_database"
        )
        .fallbackToDestructiveMigration(false)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)

        return builder.build()
    }

    override suspend fun closeDatabase() {
        withContext(Dispatchers.IO) {
            database?.close()
            database = null
        }
    }

    override suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            database?.clearAllTables()
        }
    }

    override fun getDatabaseVersion(): Int {
        return database?.openHelper?.readableDatabase?.version ?: 1
    }

    override fun isOpen(): Boolean = database?.isOpen ?: false

    override fun close() { 
        database?.close() 
    }

    companion object {
        const val DATABASE_TAG = "DATABASE"
    }
} 