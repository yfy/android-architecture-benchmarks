package com.yfy.basearchitecture.core.network.api

sealed class NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>()
    data class Error(val exception: NetworkError) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
} 