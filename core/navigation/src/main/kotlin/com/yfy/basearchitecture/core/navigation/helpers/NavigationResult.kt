package com.yfy.basearchitecture.core.navigation.helpers

sealed class NavigationResult {
    object Success : NavigationResult()
    data class Error(val message: String, val throwable: Throwable? = null) : NavigationResult()
}