package com.yfy.basearchitecture.core.database.api

data class DatabaseConfig(
    val databaseName: String,
    val version: Int,
    val enableLogging: Boolean = false,
    val fallbackToDestructiveMigration: Boolean = false,
    val enableWAL: Boolean = true,
    val enableForeignKeys: Boolean = true
)