package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.cart

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcModel
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.GetCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.RemoveFromCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.UpdateCartItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CartModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : BaseMvcModel() {
    
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()
    
    suspend fun loadCart() {
        _state.update { it.copy(isLoading = true) }
        try {
            val result = getCartUseCase(Unit)
            result.collect { items ->
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
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }
    
    suspend fun updateQuantity(itemId: String, quantity: Int) {
        try {
            updateCartItemUseCase(UpdateCartItemUseCase.Parameters(itemId, quantity))
                .collect{}
            loadCart()
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message) }
        }
    }
    
    suspend fun removeItem(itemId: String) {
        try {
            removeFromCartUseCase(RemoveFromCartUseCase.Parameters(itemId))
                .collect{}
            loadCart()
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message) }
        }
    }
}
