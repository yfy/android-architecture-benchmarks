package com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.cart.api.CheckoutRepository
import com.yfy.basearchitecture.feature.cart.api.model.Address
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAddressesUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository
) : BaseFlowUseCase<Unit, List<Address>>() {
    
    override fun execute(parameters: Unit): Flow<List<Address>> {
        return checkoutRepository.getAddresses()
    }
}
