package com.yfy.basearchitecture.feature.product.impl.mvp.ui.list

import com.yfy.basearchitecture.feature.product.api.model.Category
import com.yfy.basearchitecture.feature.product.api.model.Product

interface ProductListView {
    fun showProducts(products: List<Product>)
    fun showCategories(categories: List<Category>)
    fun showLoading()
    fun hideLoading()
    fun showLoadingMore()
    fun hideLoadingMore()
    fun showError(message: String)
    fun showCartItemCount(count: Int)
    fun navigateToDetail(productId: String)
}
