package com.yfy.basearchitecture.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.navigation.constants.Destinations
import com.yfy.basearchitecture.di.NavigationRouteRegistry


@Composable
fun AppNavHost(
    navigationRegistry: NavigationRouteRegistry,
    navManager: NavigationManager
) {
    val navController = rememberNavController()
    navManager.setNavController(navController)

    NavHost(
        navController = navController,
        startDestination = Destinations.product_list
    ) {
        navigationRegistry.getNavGraphBuilders().forEach { builder ->
            with(builder) {
                buildNavGraph(navManager)
            }
        }
    }
}