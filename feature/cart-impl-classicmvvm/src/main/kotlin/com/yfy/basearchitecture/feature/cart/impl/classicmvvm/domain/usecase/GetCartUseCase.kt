package com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : BaseFlowUseCase<Unit, List<CartItem>>() {
    
    override fun execute(parameters: Unit): Flow<List<CartItem>> {
        return cartRepository.getCart()
    }
}
