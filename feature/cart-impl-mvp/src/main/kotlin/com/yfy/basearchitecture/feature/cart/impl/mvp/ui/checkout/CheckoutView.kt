package com.yfy.basearchitecture.feature.cart.impl.mvp.ui.checkout

import com.yfy.basearchitecture.feature.cart.api.model.Address
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep

interface CheckoutView {
    fun showAddresses(addresses: List<Address>)
    fun showCartItems(items: List<CartItem>)
    fun showTotals(subtotal: Double, shipping: Double, total: Double)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun updateStep(step: CheckoutStep)
    fun showOrderSuccess()
}
