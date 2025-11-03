package com.yfy.basearchitecture.feature.cart.impl.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
): BaseFlowUseCase<RemoveFromCartUseCase.Parameters, Unit>() {

    data class Parameters(
        val cartItemId: String
    )

    override fun execute(parameters: Parameters): Flow<Unit> {
        return cartRepository.removeFromCart(parameters.cartItemId)
    }
}
