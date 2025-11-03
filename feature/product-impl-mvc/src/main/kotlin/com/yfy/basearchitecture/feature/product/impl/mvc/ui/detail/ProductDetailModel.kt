package com.yfy.basearchitecture.feature.product.impl.mvc.ui.detail

import android.content.Context
import android.widget.Toast
import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcModel
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.mvc.domain.usecase.GetProductDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ProductDetailModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val cartRepository: CartRepository,
    @ApplicationContext private val context: Context
) : BaseMvcModel() {
    
    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()
    
    suspend fun loadProduct(productId: String) {
        _state.update { it.copy(isLoading = true) }
        try {
            val result = getProductDetailUseCase(GetProductDetailUseCase.Parameters(productId))
            result.collect { product ->
                _state.update { it.copy(product = product, isLoading = false) }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    suspend fun addToCart(product: Product, quantity: Int) {
        try {
            val result = cartRepository.addToCart(product, quantity)
            result.collect{}
            Toast.makeText(context, "$quantity amount added cart!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false) }
        }
    }
    
    fun updateQuantity(quantity: Int) {
        _state.update { it.copy(quantity = quantity) }
    }
}
