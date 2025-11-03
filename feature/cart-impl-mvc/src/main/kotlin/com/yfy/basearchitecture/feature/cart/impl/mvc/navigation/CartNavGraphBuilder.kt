package com.yfy.basearchitecture.feature.cart.impl.mvc.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.cart.impl.mvc.ui.cart.CartScreen
import com.yfy.basearchitecture.feature.cart.impl.mvc.ui.checkout.CheckoutScreen
import javax.inject.Inject

class CartNavGraphBuilder @Inject constructor(
    private val navigation: com.yfy.basearchitecture.feature.cart.api.CartNavigation
) : FeatureNavGraphBuilder {
    override fun NavGraphBuilder.buildNavGraph(navManager: NavigationManager) {
        composable(CartDestinations.CART) { 
            CartScreen(navigation = navigation)
        }
        composable(CartDestinations.CHECKOUT) { 
            CheckoutScreen(navigation = navigation)
        }
    }
}
