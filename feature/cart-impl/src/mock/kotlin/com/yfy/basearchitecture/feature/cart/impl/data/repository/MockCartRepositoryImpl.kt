package com.yfy.basearchitecture.feature.cart.impl.data.repository

import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.impl.data.local.CartSessionManager
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockCartRepositoryImpl @Inject constructor(
    private val cartSessionManager: CartSessionManager
) : CartRepository {

    override fun getCart(): Flow<List<CartItem>> = cartSessionManager.cartItems

    override fun addToCart(product: Product, quantity: Int): Flow<Unit> = flow {
        cartSessionManager.addToCart(
            product,
            quantity = quantity
        )
        emit(Unit)
    }

    override fun updateCartItem(cartItemId: String, quantity: Int): Flow<Unit> = flow {
        cartSessionManager.updateCartItem(cartItemId, quantity)
    }

    override fun removeFromCart(cartItemId: String): Flow<Unit> = flow {
        cartSessionManager.removeFromCart(cartItemId)
    }

    override fun clearCart(): Flow<Unit> = flow {
        cartSessionManager.clearCart()
    }
}