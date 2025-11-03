package com.yfy.basearchitecture.feature.product.impl.mvc.navigation

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductNavigationImpl @Inject constructor(
    private val navigationManager: NavigationManager
) : ProductNavigation {
    override fun navigateToProductList() {
        navigationManager.navigate(ProductDestinations.PRODUCT_LIST)
    }
    
    override fun navigateToProductDetail(productId: String) {
        navigationManager.navigate(ProductDestinations.productDetailRoute(productId))
    }
    
    override fun navigateBack() {
        navigationManager.navigateUp()
    }

    override fun navigateToCart() {
        navigationManager.navigate("cart")
    }

    override fun navigateToChat() {
        navigationManager.navigate("chat_list")
    }
}
