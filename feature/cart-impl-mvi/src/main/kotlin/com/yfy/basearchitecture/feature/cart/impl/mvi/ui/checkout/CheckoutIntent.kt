package com.yfy.basearchitecture.feature.cart.impl.mvi.ui.checkout

sealed interface CheckoutIntent {
    object LoadData : CheckoutIntent
    data class AddressSelected(val addressId: String) : CheckoutIntent
    data class PaymentSelected(val payment: String) : CheckoutIntent
    object NextStep : CheckoutIntent
    object PreviousStep : CheckoutIntent
    object PlaceOrder : CheckoutIntent
    object NavigateBack : CheckoutIntent
}
