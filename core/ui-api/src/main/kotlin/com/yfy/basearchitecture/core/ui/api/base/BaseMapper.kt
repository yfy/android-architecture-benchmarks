package com.yfy.basearchitecture.core.ui.api.base

/**
 * Base mapper interface for converting between different data models
 */
interface BaseMapper<in T, out R> {
    
    /**
     * Map from source to destination
     */
    fun map(source: T): R
    
    /**
     * Map list from source to destination
     */
    fun mapList(source: List<T>): List<R> {
        return source.map { map(it) }
    }
    
    /**
     * Map nullable source to nullable destination
     */
    fun mapNullable(source: T?): R? {
        return source?.let { map(it) }
    }
}

/**
 * Bidirectional mapper interface for two-way conversion
 */
interface BidirectionalMapper<T, R> : BaseMapper<T, R> {
    
    /**
     * Map from destination back to source
     */
    fun mapReverse(source: R): T
    
    /**
     * Map list from destination back to source
     */
    fun mapListReverse(source: List<R>): List<T> {
        return source.map { mapReverse(it) }
    }
    
    /**
     * Map nullable destination back to nullable source
     */
    fun mapNullableReverse(source: R?): T? {
        return source?.let { mapReverse(it) }
    }
} 