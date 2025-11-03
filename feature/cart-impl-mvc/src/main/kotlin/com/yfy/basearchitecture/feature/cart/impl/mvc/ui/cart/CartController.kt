package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.cart

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcController
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CartController @Inject constructor(
    model: CartModel,
    private val navigation: CartNavigation
) : BaseMvcController<CartModel>(model) {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        scope.launch { model.loadCart() }
    }
    
    fun onUpdateQuantity(itemId: String, quantity: Int) {
        scope.launch { model.updateQuantity(itemId, quantity) }
    }
    
    fun onRemoveItem(itemId: String) {
        scope.launch { model.removeItem(itemId) }
    }
    
    fun onCheckoutClick() {
        navigation.navigateToCheckout()
    }
    
    fun onBackClick() {
        navigation.navigateBack()
    }
    
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
