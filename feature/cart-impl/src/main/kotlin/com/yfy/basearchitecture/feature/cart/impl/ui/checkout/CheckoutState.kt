package com.yfy.basearchitecture.feature.cart.impl.ui.checkout

import com.yfy.basearchitecture.feature.cart.api.model.Address
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep

data class CheckoutState(
    val currentStep: CheckoutStep = CheckoutStep.ADDRESS,
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val selectedPayment: String? = null,
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val total: Double = 0.0
) {
    val canProceed: Boolean
        get() = when (currentStep) {
            CheckoutStep.ADDRESS -> selectedAddress != null
            CheckoutStep.PAYMENT -> selectedPayment != null
            CheckoutStep.CONFIRMATION -> true
        }
}
