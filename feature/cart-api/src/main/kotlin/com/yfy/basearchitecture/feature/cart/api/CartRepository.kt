package com.yfy.basearchitecture.feature.cart.api

import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(): Flow<List<CartItem>>
    fun addToCart(product: Product, quantity: Int) :Flow<Unit>
    fun updateCartItem(cartItemId: String, quantity: Int) :Flow<Unit>
    fun removeFromCart(cartItemId: String) :Flow<Unit>
    fun clearCart() :Flow<Unit>
}
