package com.yfy.basearchitecture.core.database.api

/**
 * Base entity class for all database entities.
 * Provides common fields like id, createdAt, and updatedAt.
 * Room annotations are implemented in the impl module.
 */
abstract class BaseEntity(
    open var id: Long = 0L,
    open var createdAt: Long = System.currentTimeMillis(),
    open var updatedAt: Long = System.currentTimeMillis()
)