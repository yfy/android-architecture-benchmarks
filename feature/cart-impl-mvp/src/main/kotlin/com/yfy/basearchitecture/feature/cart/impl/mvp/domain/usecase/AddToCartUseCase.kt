package com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
):BaseFlowUseCase<AddToCartUseCase.Parameters,Unit>() {
    
    data class Parameters(
        val product: Product,
        val quantity: Int
    )

    override fun execute(parameters: Parameters): Flow<Unit> {
       return cartRepository.addToCart(parameters.product, parameters.quantity)
    }
}
