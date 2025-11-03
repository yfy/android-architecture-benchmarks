package com.yfy.basearchitecture.core.database.api

sealed class QueryResult<out T> {
    data class Success<T>(val data: T) : QueryResult<T>()
    data class Error(val exception: Throwable, val message: String) : QueryResult<Nothing>()
    object Loading : QueryResult<Nothing>()
    object Empty : QueryResult<Nothing>()
}