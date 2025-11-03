package com.yfy.basearchitecture.feature.cart.impl.ui.cart

import com.yfy.basearchitecture.feature.cart.api.model.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val total: Double = 0.0,
    val isLoading: Boolean = true
)
