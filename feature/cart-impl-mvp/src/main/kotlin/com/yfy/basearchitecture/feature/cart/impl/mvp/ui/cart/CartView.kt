package com.yfy.basearchitecture.feature.cart.impl.mvp.ui.cart

import com.yfy.basearchitecture.feature.cart.api.model.CartItem

interface CartView {
    fun showCartItems(items: List<CartItem>)
    fun showTotals(subtotal: Double, shipping: Double, total: Double)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun showEmptyCart()
}
