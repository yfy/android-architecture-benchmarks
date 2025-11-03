package com.yfy.basearchitecture.feature.product.impl.classicmvvm.ui.detail

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.domain.usecase.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val navigation: ProductNavigation,
    private val cartRepository: CartRepository
) : BaseComposeViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()
    
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()
    
    fun loadProduct(productId: String) {
        serviceLaunch {
            getProductDetailUseCase(productId)
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .catch { handleError(it) }
                .collect { product ->
                    _product.value = product
                }
        }
    }

    fun onQuantityChange(newQuantity: Int) {
        _quantity.value = newQuantity
    }

    fun addToCart(product: Product, quantity: Int) = serviceLaunch {
        cartRepository.addToCart(product, quantity)
            .catch { handleError(it) }
            .collect {
                uiHandler.showSuccess("$quantity adet sepete eklendi")
            }
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
}
