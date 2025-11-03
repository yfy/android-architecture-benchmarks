package com.yfy.basearchitecture.feature.cart.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.cart.impl.ui.cart.CartScreen
import com.yfy.basearchitecture.feature.cart.impl.ui.checkout.CheckoutScreen
import javax.inject.Inject

class CartNavGraphBuilder @Inject constructor() : FeatureNavGraphBuilder {
    override fun NavGraphBuilder.buildNavGraph(navManager: NavigationManager) {
        composable(CartDestinations.CART) { CartScreen() }
        composable(CartDestinations.CHECKOUT) { CheckoutScreen() }
    }
}
