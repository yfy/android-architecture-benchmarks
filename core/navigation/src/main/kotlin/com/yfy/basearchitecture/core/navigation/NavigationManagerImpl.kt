package com.yfy.basearchitecture.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.yfy.basearchitecture.core.navigation.deeplink.DeepLinkHandler
import com.yfy.basearchitecture.core.navigation.deeplink.LinkDetector
import com.yfy.basearchitecture.core.navigation.deeplink.LinkType
import com.yfy.basearchitecture.core.navigation.helpers.NavigationDataStore
import com.yfy.basearchitecture.core.navigation.helpers.NavigationResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManagerImpl @Inject constructor() : NavigationManager {

    companion object {
        private const val TAG = "NavigationManager"
    }

    private var _navController: NavController? = null

    override fun setNavController(navController: NavController) {
        _navController = navController
        Timber.d(TAG, "NavController set successfully")
    }

    override fun clearNavController() {
        _navController = null
        Timber.d(TAG, "NavController cleared")
    }

    override fun navigate(destination: String): NavigationResult {
        return navigate<Any?>(destination, null, null)
    }

    override fun navigate(destination: String, navOptions: NavOptions?): NavigationResult {
        return navigate<Any?>(destination, null, navOptions)
    }

    override fun <T> navigate(destination: String, data: T?): NavigationResult {
        return navigate(destination, data, null)
    }

    override fun <T> navigate(
        destination: String,
        data: T?,
        navOptions: NavOptions?
    ): NavigationResult {
        return try {
            val controller = _navController ?: return NavigationResult.Error("NavController is not set")

            if (destination.isBlank()) {
                return NavigationResult.Error("Destination cannot be empty")
            }

            when (LinkDetector.detectLinkType(destination)) {
                LinkType.DEEP_LINK, LinkType.APP_LINK -> {
                    val request = DeepLinkHandler.buildDeepLinkRequest(destination)
                        ?: return NavigationResult.Error("Invalid deep link: $destination")
                    controller.navigate(request)
                }
                LinkType.ROUTE -> {
                    val finalRoute = buildRouteWithData(destination, data)
                    controller.navigate(finalRoute, navOptions)
                }
                LinkType.INVALID -> {
                    return NavigationResult.Error("Invalid link format: $destination")
                }
            }

            NavigationResult.Success
        } catch (e: Exception) {
            Timber.i(TAG, "Navigation failed", e)
            NavigationResult.Error("Navigation failed: ${e.message}", e)
        }
    }

    override fun navigateUp(): NavigationResult {
        return try {
            val controller = _navController ?: return NavigationResult.Error("NavController is not set")
            val result = controller.navigateUp()
            if (result) NavigationResult.Success else NavigationResult.Error("Cannot navigate up")
        } catch (e: Exception) {
            Timber.i(TAG, "Navigate up failed", e)
            NavigationResult.Error("Navigate up failed: ${e.message}", e)
        }
    }

    override fun navigateToRoot(): NavigationResult {
        return try {
            val controller = _navController ?: return NavigationResult.Error("NavController is not set")
            controller.popBackStack(controller.graph.startDestinationId, false)
            NavigationResult.Success
        } catch (e: Exception) {
            Timber.i(TAG, "Navigate to root failed", e)
            NavigationResult.Error("Navigate to root failed: ${e.message}", e)
        }
    }

    override fun clearBackStack(): NavigationResult {
        return try {
            val controller = _navController ?: return NavigationResult.Error("NavController is not set")
            controller.popBackStack(controller.graph.startDestinationId, true)
            NavigationResult.Success
        } catch (e: Exception) {
            Timber.i(TAG, "Clear back stack failed", e)
            NavigationResult.Error("Clear back stack failed: ${e.message}", e)
        }
    }

    override fun handleDeepLink(deepLink: String): NavigationResult {
        return try {
            val controller = _navController ?: return NavigationResult.Error("NavController is not set")
            val request = DeepLinkHandler.buildDeepLinkRequest(deepLink)
                ?: return NavigationResult.Error("Invalid deep link: $deepLink")
            controller.navigate(request)
            NavigationResult.Success
        } catch (e: Exception) {
            Timber.i(TAG, "Deep link handling failed", e)
            NavigationResult.Error("Deep link handling failed: ${e.message}", e)
        }
    }

    private fun <T> buildRouteWithData(route: String, data: T?): String {
        return if (data != null) {
            val dataKey = NavigationDataStore.generateKey()
            NavigationDataStore.putData(dataKey, data)

            val separator = if (route.contains("?")) "&" else "?"
            "$route${separator}dataKey=$dataKey"
        } else route
    }
}