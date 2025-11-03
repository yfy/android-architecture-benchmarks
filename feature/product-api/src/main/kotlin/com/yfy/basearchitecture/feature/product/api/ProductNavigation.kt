package com.yfy.basearchitecture.feature.product.api

interface ProductNavigation {
    fun navigateToProductList()
    fun navigateToProductDetail(productId: String)
    fun navigateBack()
    fun navigateToCart()
    fun navigateToChat()
}
