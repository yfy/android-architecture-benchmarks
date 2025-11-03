package com.yfy.basearchitecture.core.database.impl.migration

import androidx.room.migration.Migration

data class DatabaseMigration(
    val fromVersion: Int,
    val toVersion: Int,
    val migration: Migration
)