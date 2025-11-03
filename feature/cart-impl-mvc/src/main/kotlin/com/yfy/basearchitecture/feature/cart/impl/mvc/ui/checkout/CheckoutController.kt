package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.checkout

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcController
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckoutController @Inject constructor(
    model: CheckoutModel,
    private val navigation: CartNavigation
) : BaseMvcController<CheckoutModel>(model) {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        scope.launch { model.loadData() }
    }
    
    fun onAddressSelected(addressId: String) {
        model.selectAddress(addressId)
    }
    
    fun onPaymentSelected(payment: String) {
        model.selectPayment(payment)
    }
    
    fun onNextStep() {
        model.nextStep()
    }
    
    fun onPreviousStep() {
        model.previousStep()
    }
    
    fun onPlaceOrder() {
        scope.launch {
            model.placeOrder()
            navigation.navigateBack()
        }
    }
    
    fun onBackClick() {
        navigation.navigateBack()
    }
    
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
