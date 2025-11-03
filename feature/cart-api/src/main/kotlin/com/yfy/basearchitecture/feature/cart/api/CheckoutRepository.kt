package com.yfy.basearchitecture.feature.cart.api

import com.yfy.basearchitecture.feature.cart.api.model.Address
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.Order
import kotlinx.coroutines.flow.Flow

interface CheckoutRepository {
    fun getAddresses(): Flow<List<Address>>
    fun createOrder(items: List<CartItem>, addressId: String): Flow<Order>
}
