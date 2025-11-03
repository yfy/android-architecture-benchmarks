package com.yfy.basearchitecture.feature.cart.impl.mvi.navigation

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import javax.inject.Inject

class CartNavigationImpl @Inject constructor(
    private val navigationManager: NavigationManager
) : CartNavigation {
    override fun navigateToCart() {
        navigationManager.navigate(CartDestinations.CART)
    }

    override fun navigateToCheckout() {
        navigationManager.navigate(CartDestinations.CHECKOUT)
    }

    override fun navigateBack() {
        navigationManager.navigateUp()
    }
}
