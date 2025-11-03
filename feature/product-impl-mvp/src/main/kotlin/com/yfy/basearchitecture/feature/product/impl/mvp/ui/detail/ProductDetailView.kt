package com.yfy.basearchitecture.feature.product.impl.mvp.ui.detail

import com.yfy.basearchitecture.feature.product.api.model.Product

interface ProductDetailView {
    fun showProduct(product: Product)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun showAddedToCart()
    fun navigateBack()
}
