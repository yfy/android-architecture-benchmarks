package com.yfy.basearchitecture.feature.product.impl.mvi.navigation

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import javax.inject.Inject

class ProductNavigationImpl @Inject constructor(
    private val navigationManager: NavigationManager,
    private val cartNavigation: CartNavigation
) : ProductNavigation {
    override fun navigateToProductList() {
        navigationManager.navigate(ProductDestinations.PRODUCT_LIST)
    }

    override fun navigateToProductDetail(productId: String) {
        navigationManager.navigate(ProductDestinations.productDetailRoute(productId))
    }

    override fun navigateToCart() {
        cartNavigation.navigateToCart()
    }

    override fun navigateBack() {
        navigationManager.navigateUp()
    }

    override fun navigateToChat() {
        navigationManager.navigate("chat_list")
    }
}
