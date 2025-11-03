package com.yfy.basearchitecture.feature.product.impl.mvi.ui.detail

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.mvi.domain.usecase.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val navigation: ProductNavigation,
    private val cartRepository: CartRepository
) : BaseComposeViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    fun handleIntent(intent: ProductDetailIntent) {
        when (intent) {
            is ProductDetailIntent.LoadProduct -> loadProduct(intent.productId)
            is ProductDetailIntent.QuantityChanged -> onQuantityChange(intent.quantity)
            is ProductDetailIntent.AddToCart -> addToCart()
            is ProductDetailIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadProduct(productId: String) {
        serviceLaunch {
            getProductDetailUseCase(productId)
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .catch { handleError(it) }
                .collect { product ->
                    _state.update { it.copy(product = product) }
                }
        }
    }

    private fun onQuantityChange(newQuantity: Int) {
        _state.update { it.copy(quantity = newQuantity) }
    }

    private fun addToCart() {
        val product = _state.value.product ?: return
        val quantity = _state.value.quantity
        
        serviceLaunch {
            cartRepository.addToCart(product, quantity)
                .catch { handleError(it) }
                .collect {
                    uiHandler.showSuccess("$quantity adet sepete eklendi")
                }
        }
    }

    private fun navigateBack() {
        navigation.navigateBack()
    }
}
