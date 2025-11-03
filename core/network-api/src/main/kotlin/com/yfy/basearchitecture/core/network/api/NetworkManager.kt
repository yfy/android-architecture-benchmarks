package com.yfy.basearchitecture.core.network.api

import kotlinx.coroutines.flow.Flow

interface NetworkManager {

    fun <T> createService(serviceClass: Class<T>): T

    suspend fun <T> executeRequest(request: suspend () -> T): Flow<NetworkResponse<T>>

    fun getBaseUrl(): String
}