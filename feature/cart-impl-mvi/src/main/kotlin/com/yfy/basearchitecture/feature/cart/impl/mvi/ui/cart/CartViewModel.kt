package com.yfy.basearchitecture.feature.cart.impl.mvi.ui.cart

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.impl.mvi.domain.usecase.GetCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvi.domain.usecase.RemoveFromCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvi.domain.usecase.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val navigation: CartNavigation
) : BaseComposeViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        handleIntent(CartIntent.LoadCart)
    }

    fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.LoadCart -> loadCart()
            is CartIntent.UpdateQuantity -> updateQuantity(intent.itemId, intent.quantity)
            is CartIntent.RemoveItem -> removeItem(intent.itemId)
            is CartIntent.NavigateToCheckout -> navigation.navigateToCheckout()
            is CartIntent.NavigateBack -> navigation.navigateBack()
        }
    }

    private fun loadCart() {
        serviceLaunch {
            getCartUseCase(Unit)
                .catch { handleError(it) }
                .collect { items ->
                    val subtotal = items.sumOf { it.price * it.quantity }
                    val shippingCost = if (subtotal > 150) 0.0 else 29.99
                    _state.update { 
                        it.copy(
                            items = items, 
                            subtotal = subtotal, 
                            shippingCost = shippingCost, 
                            total = subtotal + shippingCost,
                            isLoading = false
                        ) 
                    }
                }
        }
    }

    private fun updateQuantity(cartItemId: String, quantity: Int) {
        serviceLaunch {
            updateCartItemUseCase(UpdateCartItemUseCase.Parameters(cartItemId, quantity))
                .collect{}
        }
    }

    private fun removeItem(cartItemId: String) {
        serviceLaunch {
            removeFromCartUseCase(RemoveFromCartUseCase.Parameters(cartItemId))
                .collect{}
        }
    }
}
