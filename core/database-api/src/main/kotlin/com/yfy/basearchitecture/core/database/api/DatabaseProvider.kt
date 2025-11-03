package com.yfy.basearchitecture.core.database.api

import androidx.room.RoomDatabase

/**
 * Base interface for the application database.
 * Implementations should provide concrete DAOs and entities.
 */
interface DatabaseProvider {
    /**
     * Ana veritabanı instance'ını döndürür
     */
    fun getDatabase(): RoomDatabase

    /**
     * Veritabanını kapatır
     */
    suspend fun closeDatabase()

    /**
     * Veritabanını temizler (test ortamı için)
     */
    suspend fun clearDatabase()

    /**
     * Veritabanı versiyonunu döndürür
     */
    fun getDatabaseVersion(): Int
    
    /**
     * Returns true if the database is open and ready for use
     */
    fun isOpen(): Boolean

    /**
     * Closes the database and releases all resources
     */
    fun close()
} 