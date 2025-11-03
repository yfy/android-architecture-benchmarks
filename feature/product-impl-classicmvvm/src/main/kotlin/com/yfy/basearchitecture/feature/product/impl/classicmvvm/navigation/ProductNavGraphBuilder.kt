package com.yfy.basearchitecture.feature.product.impl.classicmvvm.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.ui.detail.ProductDetailScreen
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.ui.list.ProductListScreen
import javax.inject.Inject

class ProductNavGraphBuilder @Inject constructor() : FeatureNavGraphBuilder {
    override fun NavGraphBuilder.buildNavGraph(navManager: NavigationManager) {
        composable(ProductDestinations.PRODUCT_LIST) {
            ProductListScreen()
        }
        composable(
            route = ProductDestinations.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(productId = productId)
        }
    }
}
