package com.yfy.basearchitecture.feature.cart.impl.mvp.ui.checkout

import android.content.Context
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.base.BasePresenterImpl
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.ClearCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.CreateOrderUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.GetAddressesUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.GetCartUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckoutPresenter @Inject constructor(
    navigationManager: NavigationManager,
    private val getCartUseCase: GetCartUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val navigation: CartNavigation,
    @ApplicationContext private val context: Context
) : BasePresenterImpl(navigationManager) {
    private var view: CheckoutView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var currentStep = CheckoutStep.ADDRESS
    private var selectedAddressId: String? = null
    private var selectedPayment: String? = null

    fun attachView(view: CheckoutView) {
        this.view = view
        loadData()
    }

    fun detachView() {
        view = null
        scope.cancel()
    }

    override fun onViewCreated() {
        super.onViewCreated()
        logScreen("CheckoutScreen")
    }

    override fun onViewDestroyed() {
        super.onViewDestroyed()
        detachView()
    }

    private fun loadData() {
        scope.launch {
            setLoading(true)

            combine(getCartUseCase(Unit), getAddressesUseCase(Unit)) { cart, addresses ->
                Pair(cart, addresses)
            }
                .catch { error ->
                    view?.showError(error.localizedMessage ?: "Unknown error")
                }
                .collect { (cart, addresses) ->
                    setLoading(false)
                    view?.showCartItems(cart)
                    view?.showAddresses(addresses)

                    // Set default address
                    val defaultAddress = addresses.find { it.isDefault }
                    if (defaultAddress != null) {
                        selectedAddressId = defaultAddress.id
                    }

                    val subtotal = cart.sumOf { it.price * it.quantity }
                    val shippingCost = if (subtotal > 150) 0.0 else 29.99
                    view?.showTotals(subtotal, shippingCost, subtotal + shippingCost)
                    view?.updateStep(currentStep)
                }
        }
    }

    fun selectAddress(addressId: String) {
        selectedAddressId = addressId
    }

    fun selectPayment(payment: String) {
        selectedPayment = payment
    }

    fun nextStep() {
        currentStep = when (currentStep) {
            CheckoutStep.ADDRESS -> CheckoutStep.PAYMENT
            CheckoutStep.PAYMENT -> CheckoutStep.CONFIRMATION
            CheckoutStep.CONFIRMATION -> CheckoutStep.CONFIRMATION
        }
        view?.updateStep(currentStep)
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            CheckoutStep.ADDRESS -> CheckoutStep.ADDRESS
            CheckoutStep.PAYMENT -> CheckoutStep.ADDRESS
            CheckoutStep.CONFIRMATION -> CheckoutStep.PAYMENT
        }
        view?.updateStep(currentStep)
    }

    fun placeOrder() {
        if (selectedAddressId == null) return

        scope.launch {
            try {
                view?.showLoading()

                getCartUseCase(Unit)
                    .catch { error ->
                        view?.showError(error.localizedMessage ?: "Failed to get cart")
                    }
                    .collect { cartItems ->
                        val order = createOrderUseCase(
                            CreateOrderUseCase.Parameters(
                                cartItems,
                                selectedAddressId!!
                            )
                        )

                        clearCartUseCase(Unit)
                            .catch { error ->
                                view?.showError(error.localizedMessage ?: "Failed to clear cart")
                            }
                            .collect {}
                        showToast(context, "Order Success")
                        navigation.navigateBack()
                    }
            } catch (e: Exception) {
                view?.showError("Failed to place order")
            } finally {
                view?.hideLoading()
            }
        }
    }

    fun navigateBack() {
        navigation.navigateBack()
    }

    fun canProceed(): Boolean {
        return when (currentStep) {
            CheckoutStep.ADDRESS -> selectedAddressId != null
            CheckoutStep.PAYMENT -> selectedPayment != null
            CheckoutStep.CONFIRMATION -> true
        }
    }
}
