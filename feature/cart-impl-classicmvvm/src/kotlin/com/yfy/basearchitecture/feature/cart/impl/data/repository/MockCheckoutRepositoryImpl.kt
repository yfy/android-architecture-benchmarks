package com.yfy.basearchitecture.feature.cart.impl.data.repository

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.base.BaseRepository
import com.yfy.basearchitecture.core.ui.api.extensions.getJson
import com.yfy.basearchitecture.feature.cart.api.CheckoutRepository
import com.yfy.basearchitecture.feature.cart.api.model.Address
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.Order
import com.yfy.basearchitecture.feature.cart.api.model.OrderStatus
import com.yfy.basearchitecture.feature.cart.impl.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockCheckoutRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseRepository(), CheckoutRepository {

    override fun getAddresses(): Flow<List<Address>> = sendRequest {
        context.getJson(file = R.raw.addresses)
    }

    override fun createOrder(items: List<CartItem>, addressId: String): Flow<Order> = flow {
        val addresses: List<Address> = context.getJson(file = R.raw.addresses)
        val address = addresses.find { it.id == addressId } ?: throw Exception("Address not found")
        val subtotal = items.sumOf { it.price * it.quantity }
        val shippingCost = if (subtotal > 150) 0.0 else 29.99
        Order(
            id = "order_${System.currentTimeMillis()}",
            items = items,
            address = address,
            subtotal = subtotal,
            shippingCost = shippingCost,
            total = subtotal + shippingCost,
            status = OrderStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
    }
}
