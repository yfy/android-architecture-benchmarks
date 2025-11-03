package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.checkout

import android.content.Context
import android.widget.Toast
import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcModel
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.ClearCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.CreateOrderUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.GetAddressesUseCase
import com.yfy.basearchitecture.feature.cart.impl.domain.usecase.GetCartUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckoutModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    @ApplicationContext private val context: Context
) : BaseMvcModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    suspend fun loadData() {
        _state.update { it.copy(isLoading = true) }
        try {
            // Load cart items
            getCartUseCase(Unit).combine(getAddressesUseCase(Unit)) { items, addresses ->
                Pair(items, addresses)
            }.collect { (items, addresses) ->
                val subtotal = items.sumOf { it.price * it.quantity }
                val shippingCost = if (subtotal > 150) 0.0 else 29.99
                _state.update {
                    it.copy(
                        cartItems = items,
                        subtotal = subtotal,
                        shippingCost = shippingCost,
                        total = subtotal + shippingCost,
                        addresses = addresses,
                        isLoading = false
                    )
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
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

    suspend fun placeOrder() {
        _state.update { it.copy(isLoading = true) }
        try {
            val order = createOrderUseCase(
                CreateOrderUseCase.Parameters(
                    addressId = _state.value.selectedAddress?.id ?: "",
                    items = _state.value.cartItems
                )
            )
            clearCartUseCase(Unit).collect {}
            _state.update { it.copy(isLoading = false) }
            modelScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Order Success!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }
}
