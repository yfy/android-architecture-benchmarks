package com.yfy.basearchitecture.core.database.impl.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MigrationManager @Inject constructor() {

    fun getMigrationStrategy(): MigrationStrategy {
        return MigrationStrategy.FallbackToDestructive(
            migrations = listOf(
                // Example migrations for use
                // DatabaseMigration(1, 2, MIGRATION_1_2)
            )
        )
    }

    // Example Migration
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration script will write here
                // database.execSQL("ALTER TABLE users ADD COLUMN phone_number TEXT")
            }
        }
    }
}