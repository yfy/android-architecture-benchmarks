package com.yfy.basearchitecture.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import com.yfy.basearchitecture.core.navigation.deeplink.DeepLinkHandler
import com.yfy.basearchitecture.core.navigation.deeplink.LinkDetector
import com.yfy.basearchitecture.core.navigation.deeplink.LinkType
import com.yfy.basearchitecture.core.navigation.helpers.NavigationDataStore
import com.yfy.basearchitecture.core.navigation.helpers.NavigationResult

// Universal navigate extension
fun NavController.navigate(
    destination: String,
    data: Any? = null,
    navOptions: NavOptions? = null
): NavigationResult {
    return try {
        val linkType = LinkDetector.detectLinkType(destination)

        when (linkType) {
            LinkType.DEEP_LINK, LinkType.APP_LINK -> {
                val request = DeepLinkHandler.buildDeepLinkRequest(destination)
                    ?: return NavigationResult.Error("Invalid deep link: $destination")
                navigate(request)
            }
            LinkType.ROUTE -> {
                val finalRoute = buildRouteWithData(destination, data)
                navigate(finalRoute, navOptions)
            }
            LinkType.INVALID -> {
                return NavigationResult.Error("Invalid link format: $destination")
            }
        }
        NavigationResult.Success
    } catch (e: Exception) {
        NavigationResult.Error("Navigation failed: ${e.message}", e)
    }
}

// Context Extensions
fun Context.navigate(destination: String, data: Any? = null): NavigationResult {
    return try {
        val linkType = LinkDetector.detectLinkType(destination)

        when (linkType) {
            LinkType.DEEP_LINK, LinkType.APP_LINK -> {
                val intent = Intent(Intent.ACTION_VIEW, destination.toUri())
                if (data != null) {
                    val dataKey = NavigationDataStore.generateKey()
                    NavigationDataStore.putData(dataKey, data)
                    intent.putExtra("dataKey", dataKey)
                }
                startActivity(intent)
            }
            LinkType.ROUTE -> {
                return NavigationResult.Error("Route navigation from context needs NavController")
            }
            LinkType.INVALID -> {
                return NavigationResult.Error("Invalid link format: $destination")
            }
        }
        NavigationResult.Success
    } catch (e: Exception) {
        NavigationResult.Error("Context navigation failed: ${e.message}", e)
    }
}

// Fragment Extensions
fun Fragment.navigate(destination: String, data: Any? = null, navOptions: NavOptions? = null): NavigationResult {
    return findNavController(requireView()).navigate(destination, data, navOptions)
}

fun Fragment.navigateUp(): NavigationResult {
    return try {
        val result = findNavController(requireView()).navigateUp()
        if (result) NavigationResult.Success else NavigationResult.Error("Cannot navigate up")
    } catch (e: Exception) {
        NavigationResult.Error("Navigate up failed: ${e.message}", e)
    }
}

fun Fragment.navigateToRoot(): NavigationResult {
    return try {
        val navController = findNavController(requireView())
        navController.popBackStack(navController.graph.startDestinationId, false)
        NavigationResult.Success
    } catch (e: Exception) {
        NavigationResult.Error("Navigate to root failed: ${e.message}", e)
    }
}

// Activity Extensions
fun ComponentActivity.navigate(destination: String, data: Any? = null): NavigationResult {
    return try {
        val linkType = LinkDetector.detectLinkType(destination)

        when (linkType) {
            LinkType.DEEP_LINK, LinkType.APP_LINK -> {
                val intent = Intent(Intent.ACTION_VIEW, destination.toUri())
                if (data != null) {
                    val dataKey = NavigationDataStore.generateKey()
                    NavigationDataStore.putData(dataKey, data)
                    intent.putExtra("dataKey", dataKey)
                }
                startActivity(intent)
            }
            LinkType.ROUTE -> {
                return NavigationResult.Error("Route navigation from activity needs NavController")
            }
            LinkType.INVALID -> {
                return NavigationResult.Error("Invalid link format: $destination")
            }
        }
        NavigationResult.Success
    } catch (e: Exception) {
        NavigationResult.Error("Activity navigation failed: ${e.message}", e)
    }
}

// Navigation with custom data types
inline fun <reified T> NavController.navigateWithData(
    destination: String,
    data: T,
    navOptions: NavOptions? = null
): NavigationResult {
    return navigate(destination, data, navOptions)
}

inline fun <reified T> Fragment.navigateWithData(
    destination: String,
    data: T,
    navOptions: NavOptions? = null
): NavigationResult {
    return navigate(destination, data, navOptions)
}

// Data retrieval extensions
inline fun <reified T> Fragment.getNavigationData(): T? {
    val dataKey = arguments?.getString("dataKey")
    return dataKey?.let { NavigationDataStore.getData<T>(it) }
}

inline fun <reified T> Activity.getNavigationData(): T? {
    val dataKey = intent.getStringExtra("dataKey")
    return dataKey?.let { NavigationDataStore.getData<T>(it) }
}

// Utility extensions
fun NavController.clearNavigationData() {
    NavigationDataStore.clearData()
}

fun NavController.popBackStackWithData(route: String, data: Any? = null): NavigationResult {
    return try {
        if (data != null) {
            val dataKey = NavigationDataStore.generateKey()
            NavigationDataStore.putData(dataKey, data)
            currentBackStackEntry?.savedStateHandle?.set("dataKey", dataKey)
        }
        popBackStack(route, false)
        NavigationResult.Success
    } catch (e: Exception) {
        NavigationResult.Error("Pop back stack failed: ${e.message}", e)
    }
}

// Safe navigation extensions
fun NavController.safeNavigate(destination: String, data: Any? = null): NavigationResult {
    return navigate(destination, data)
}

fun Fragment.safeNavigate(destination: String, data: Any? = null): NavigationResult {
    return navigate(destination, data)
}

// Helper Functions
private fun buildRouteWithData(route: String, data: Any?): String {
    return if (data != null) {
        val dataKey = NavigationDataStore.generateKey()
        NavigationDataStore.putData(dataKey, data)

        val separator = if (route.contains("?")) "&" else "?"
        "$route${separator}dataKey=$dataKey"
    } else route
}