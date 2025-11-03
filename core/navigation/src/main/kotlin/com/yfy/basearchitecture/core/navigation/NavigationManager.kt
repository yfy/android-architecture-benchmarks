package com.yfy.basearchitecture.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.yfy.basearchitecture.core.navigation.helpers.NavigationResult

interface NavigationManager {
    fun setNavController(navController: NavController)
    fun clearNavController()
    
    fun navigate(destination: String): NavigationResult
    fun navigate(destination: String, navOptions: NavOptions?): NavigationResult
    fun <T> navigate(destination: String, data: T?): NavigationResult
    fun <T> navigate(destination: String, data: T?, navOptions: NavOptions?): NavigationResult

    fun navigateUp(): NavigationResult
    fun navigateToRoot(): NavigationResult
    fun clearBackStack(): NavigationResult
    fun handleDeepLink(deepLink: String): NavigationResult
}