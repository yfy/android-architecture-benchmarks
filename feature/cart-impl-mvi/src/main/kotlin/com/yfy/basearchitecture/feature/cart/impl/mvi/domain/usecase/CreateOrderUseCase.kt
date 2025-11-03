package com.yfy.basearchitecture.feature.cart.impl.mvi.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CheckoutRepository
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.Order
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateOrderUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository
): BaseFlowUseCase<CreateOrderUseCase.Parameters, Order>() {
    
    data class Parameters(
        val items: List<CartItem>,
        val addressId: String
    )

    override fun execute(parameters: Parameters): Flow<Order> {
        return checkoutRepository.createOrder(parameters.items, parameters.addressId)
    }
}
