package com.yfy.basearchitecture.core.database.api

import kotlinx.coroutines.flow.Flow

/**
 * Base DAO interface providing common database operations.
 * All entity-specific DAOs should extend this interface.
 * Room annotations are implemented in the impl module.
 */
interface BaseDao<T : BaseEntity> {
    
    /**
     * Insert a single entity
     */
    suspend fun insert(entity: T): Long
    
    /**
     * Insert multiple entities
     */
    suspend fun insertAll(entities: List<T>): List<Long>
    
    /**
     * Update a single entity
     */
    suspend fun update(entity: T)
    
    /**
     * Update multiple entities
     */
    suspend fun updateAll(entities: List<T>)
    
    /**
     * Delete a single entity
     */
    suspend fun delete(entity: T)
    
    /**
     * Delete multiple entities
     */
    suspend fun deleteAll(entities: List<T>)
    
    /**
     * Get entity by ID
     */
    suspend fun getById(id: Long): T?
    
    /**
     * Get all entities
     */
    suspend fun getAll(): List<T>
    
    /**
     * Get all entities as Flow
     */
    fun getAllAsFlow(): Flow<List<T>>
    
    /**
     * Delete entity by ID
     */
    suspend fun deleteById(id: Long)
    
    /**
     * Delete all entities
     */
    suspend fun deleteAll()
    
    /**
     * Count total entities
     */
    suspend fun count(): Int
    
    /**
     * Check if entity exists by ID
     */
    suspend fun exists(id: Long): Boolean
}