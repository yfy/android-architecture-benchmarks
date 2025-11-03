package com.yfy.basearchitecture.feature.cart.impl.classicmvvm.ui.checkout

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.api.model.Address
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.ClearCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.CreateOrderUseCase
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.GetAddressesUseCase
import com.yfy.basearchitecture.feature.cart.impl.classicmvvm.domain.usecase.GetCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val navigation: CartNavigation
) : BaseComposeViewModel() {
    private val _currentStep = MutableStateFlow(CheckoutStep.ADDRESS)
    val currentStep: StateFlow<CheckoutStep> = _currentStep.asStateFlow()
    
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()
    
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()
    
    private val _selectedPayment = MutableStateFlow<String?>(null)
    val selectedPayment: StateFlow<String?> = _selectedPayment.asStateFlow()
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()
    
    private val _shippingCost = MutableStateFlow(0.0)
    val shippingCost: StateFlow<Double> = _shippingCost.asStateFlow()
    
    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()
    
    val canProceed: Boolean
        get() = when (_currentStep.value) {
            CheckoutStep.ADDRESS -> _selectedAddress.value != null
            CheckoutStep.PAYMENT -> _selectedPayment.value != null
            CheckoutStep.CONFIRMATION -> true
        }

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
                    _cartItems.value = cart
                    _addresses.value = addresses
                    _selectedAddress.value = addresses.find { it.isDefault }
                    
                    val calculatedSubtotal = cart.sumOf { it.price * it.quantity }
                    val calculatedShippingCost = if (calculatedSubtotal > 150) 0.0 else 29.99
                    val calculatedTotal = calculatedSubtotal + calculatedShippingCost
                    
                    _subtotal.value = calculatedSubtotal
                    _shippingCost.value = calculatedShippingCost
                    _total.value = calculatedTotal
                    setLoading(false)
                }
        }
    }

    fun selectAddress(addressId: String) {
        _selectedAddress.value = _addresses.value.find { addr -> addr.id == addressId }
    }

    fun selectPayment(payment: String) {
        _selectedPayment.value = payment
    }

    fun nextStep() {
        val newStep = when (_currentStep.value) {
            CheckoutStep.ADDRESS -> CheckoutStep.PAYMENT
            CheckoutStep.PAYMENT -> CheckoutStep.CONFIRMATION
            CheckoutStep.CONFIRMATION -> CheckoutStep.CONFIRMATION
        }
        _currentStep.value = newStep
    }

    fun previousStep() {
        val newStep = when (_currentStep.value) {
            CheckoutStep.ADDRESS -> CheckoutStep.ADDRESS
            CheckoutStep.PAYMENT -> CheckoutStep.ADDRESS
            CheckoutStep.CONFIRMATION -> CheckoutStep.PAYMENT
        }
        _currentStep.value = newStep
    }

    fun placeOrder() {
        val selectedAddress = _selectedAddress.value ?: return
        serviceLaunch {
            val order = createOrderUseCase(CreateOrderUseCase.Parameters(_cartItems.value, selectedAddress.id))
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
