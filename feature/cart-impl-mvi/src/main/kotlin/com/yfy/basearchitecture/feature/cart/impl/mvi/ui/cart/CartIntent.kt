package com.yfy.basearchitecture.feature.cart.impl.mvi.ui.cart

sealed interface CartIntent {
    object LoadCart : CartIntent
    data class UpdateQuantity(val itemId: String, val quantity: Int) : CartIntent
    data class RemoveItem(val itemId: String) : CartIntent
    object NavigateToCheckout : CartIntent
    object NavigateBack : CartIntent
}
