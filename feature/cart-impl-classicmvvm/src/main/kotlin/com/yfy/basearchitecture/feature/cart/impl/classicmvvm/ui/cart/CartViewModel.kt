package com.yfy.basearchitecture.feature.cart.impl.classicmvvm.ui.cart

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.GetCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.RemoveFromCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.UpdateCartItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val navigation: CartNavigation
) : BaseComposeViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()
    
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()
    
    private val _shippingCost = MutableStateFlow(0.0)
    val shippingCost: StateFlow<Double> = _shippingCost.asStateFlow()
    
    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        serviceLaunch {
            setLoading(true)
            getCartUseCase(Unit)
                .catch { handleError(it) }
                .collect { items ->
                    _items.value = items
                    val calculatedSubtotal = items.sumOf { it.price * it.quantity }
                    val calculatedShippingCost = if (calculatedSubtotal > 150) 0.0 else 29.99
                    val calculatedTotal = calculatedSubtotal + calculatedShippingCost
                    
                    _subtotal.value = calculatedSubtotal
                    _shippingCost.value = calculatedShippingCost
                    _total.value = calculatedTotal
                    setLoading(false)
                }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        serviceLaunch {
            updateCartItemUseCase(UpdateCartItemUseCase.Parameters(cartItemId, quantity))
                .catch { handleError(it) }
                .collect { }
        }
    }

    fun removeItem(cartItemId: String) {
        serviceLaunch {
            removeFromCartUseCase(RemoveFromCartUseCase.Parameters(cartItemId))
                .catch { handleError(it) }
                .collect { }
        }
    }

    fun navigateToCheckout() {
        navigation.navigateToCheckout()
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
}
