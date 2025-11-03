package com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
): BaseFlowUseCase<Unit, Unit>() {

    override fun execute(parameters: Unit): Flow<Unit> {
        return cartRepository.clearCart()
    }
}
