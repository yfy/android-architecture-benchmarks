package com.yfy.basearchitecture.core.ui.api.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * Base Repository class that provides common functionality for all Repositories
 * Includes error handling and basic repository operations
 */
abstract class BaseRepository {

    fun <T>sendRequest(call: suspend() -> T): Flow<T> = flow {
        emit(call.invoke())
    }.flowOn(Dispatchers.IO)

    /**
     * Create a flow with loading state and error handling
     */
    protected fun <T> createFlow(
        block: suspend () -> T
    ): Flow<Result<T>> = flow {
        emit(Result.success(block()))
    }.onStart {
        // Could emit loading state here if needed
    }.catch { exception ->
        emit(Result.failure(exception))
    }

    /**
     * Execute network call with retry mechanism
     */
    protected suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        block: suspend () -> T
    ): Result<T> {
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            try {
                val result = block()
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                if (attempt == maxRetries - 1) {
                    return Result.failure(lastException ?: Exception("Unknown error"))
                }
                // Could add delay here for exponential backoff
            }
        }
        
        return Result.failure(lastException ?: Exception("Unknown error"))
    }

    /**
     * Safe database operation with error handling
     */
    protected suspend fun <T> safeDatabaseCall(
        block: suspend () -> T
    ): Result<T> {
        return try {
            val result = block()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Safe network operation with error handling
     */
    protected suspend fun <T> safeNetworkCall(
        block: suspend () -> T
    ): Result<T> {
        return try {
            val result = block()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 