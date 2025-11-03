package com.yfy.basearchitecture.feature.cart.impl.ui.checkout

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.ClearCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.CreateOrderUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.GetAddressesUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.GetCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val navigation: CartNavigation
) : BaseComposeViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        serviceLaunch {
            combine(getCartUseCase(Unit), getAddressesUseCase(Unit)) { cart, addresses -> 
                Pair(cart, addresses) 
            }
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .catch { handleError(it) }
                .collect { (cart, addresses) ->
                    setLoading(false)
                    val subtotal = cart.sumOf { it.price * it.quantity }
                    val shippingCost = if (subtotal > 150) 0.0 else 29.99
                    _state.update {
                        it.copy(
                            cartItems = cart,
                            addresses = addresses,
                            selectedAddress = addresses.find { it.isDefault },
                            subtotal = subtotal,
                            shippingCost = shippingCost,
                            total = subtotal + shippingCost
                        )
                    }
                }
        }
    }

    fun selectAddress(addressId: String) {
        _state.update { 
            it.copy(selectedAddress = it.addresses.find { addr -> addr.id == addressId }) 
        }
    }

    fun selectPayment(payment: String) {
        _state.update { it.copy(selectedPayment = payment) }
    }

    fun nextStep() {
        val newStep = when (_state.value.currentStep) {
            CheckoutStep.ADDRESS -> CheckoutStep.PAYMENT
            CheckoutStep.PAYMENT -> CheckoutStep.CONFIRMATION
            CheckoutStep.CONFIRMATION -> CheckoutStep.CONFIRMATION
        }
        _state.update { it.copy(currentStep = newStep) }
    }

    fun previousStep() {
        val newStep = when (_state.value.currentStep) {
            CheckoutStep.ADDRESS -> CheckoutStep.ADDRESS
            CheckoutStep.PAYMENT -> CheckoutStep.ADDRESS
            CheckoutStep.CONFIRMATION -> CheckoutStep.PAYMENT
        }
        _state.update { it.copy(currentStep = newStep) }
    }

    fun placeOrder() {
        val state = _state.value
        if (state.selectedAddress == null) return
        serviceLaunch {
            val order = createOrderUseCase(CreateOrderUseCase.Parameters(state.cartItems, state.selectedAddress.id))
            clearCartUseCase(Unit)
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .collect{}
            uiHandler.showSuccess("Sipariş oluşturuldu!")
            navigation.navigateBack()
        }
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
}
