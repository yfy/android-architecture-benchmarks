package com.yfy.basearchitecture.core.network.api

sealed class NetworkError : Exception() {
    object NetworkUnavailable : NetworkError() {
        private fun readResolve(): Any = NetworkUnavailable
    }
    object Timeout : NetworkError() {
        private fun readResolve(): Any = Timeout
    }
    data class HttpError(val code: Int, override val message: String) : NetworkError()
    data class UnknownError(val throwable: Throwable) : NetworkError() {
        override val message: String
            get() = throwable.message ?: "Unknown error occurred"
    }
} 