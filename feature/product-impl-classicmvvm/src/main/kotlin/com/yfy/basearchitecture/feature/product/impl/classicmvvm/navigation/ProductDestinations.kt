package com.yfy.basearchitecture.feature.product.impl.classicmvvm.navigation

object ProductDestinations {
    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    fun productDetailRoute(productId: String) = "product_detail/$productId"
}
