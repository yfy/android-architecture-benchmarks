package com.yfy.basearchitecture.feature.cart.impl.mvp.ui.cart

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.base.BasePresenterImpl
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.GetCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.RemoveFromCartUseCase
import com.yfy.basearchitecture.feature.cart.impl.mvp.domain.usecase.UpdateCartItemUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class CartPresenter @Inject constructor(
    navigationManager: NavigationManager,
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val navigation: CartNavigation
) : BasePresenterImpl(navigationManager) {
    private var view: CartView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attachView(view: CartView) {
        this.view = view
        loadCart()
    }

    fun detachView() {
        view = null
    }

    override fun onViewCreated() {
        super.onViewCreated()
        logScreen("CartScreen")
    }

    override fun onViewDestroyed() {
        super.onViewDestroyed()
        detachView()
    }

    private fun loadCart() {
        scope.launch {
            setLoading(true)

            getCartUseCase(Unit)
                .catch { error ->
                    view?.showError(error.localizedMessage ?: "Unknown error")
                }
                .collect { items ->
                    setLoading(false)
                    if (items.isEmpty()) {
                        view?.showEmptyCart()
                    } else {
                        view?.showCartItems(items)
                        val subtotal = items.sumOf { it.price * it.quantity }
                        val shippingCost = if (subtotal > 150) 0.0 else 29.99
                        view?.showTotals(subtotal, shippingCost, subtotal + shippingCost)
                    }
                }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        scope.launch {
            try {
                updateCartItemUseCase(UpdateCartItemUseCase.Parameters(cartItemId, quantity))
                    .catch { error ->
                        view?.showError(error.localizedMessage ?: "Failed to update quantity")
                    }
                    .collect { /* Success - cart will be reloaded automatically */ }
            } catch (e: Exception) {
                view?.showError("Failed to update quantity")
            }
        }
    }

    fun removeItem(cartItemId: String) {
        scope.launch {
            try {
                removeFromCartUseCase(RemoveFromCartUseCase.Parameters(cartItemId))
                    .catch { error ->
                        view?.showError(error.localizedMessage ?: "Failed to remove item")
                    }
                    .collect { }
            } catch (e: Exception) {
                view?.showError("Failed to remove item")
            }
        }
    }

    fun navigateToCheckout() {
        navigation.navigateToCheckout()
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
}
