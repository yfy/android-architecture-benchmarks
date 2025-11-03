package com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
): BaseFlowUseCase<UpdateCartItemUseCase.Parameters, Unit>() {
    
    data class Parameters(
        val cartItemId: String,
        val quantity: Int
    )

    override fun execute(parameters: Parameters): Flow<Unit> {
        return cartRepository.updateCartItem(parameters.cartItemId, parameters.quantity)
    }
}
