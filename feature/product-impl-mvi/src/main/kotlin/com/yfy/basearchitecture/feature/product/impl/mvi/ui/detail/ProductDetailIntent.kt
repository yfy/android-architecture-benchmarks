package com.yfy.basearchitecture.feature.product.impl.mvi.ui.detail

sealed interface ProductDetailIntent {
    data class LoadProduct(val productId: String) : ProductDetailIntent
    data class QuantityChanged(val quantity: Int) : ProductDetailIntent
    object AddToCart : ProductDetailIntent
    object NavigateBack : ProductDetailIntent
}
