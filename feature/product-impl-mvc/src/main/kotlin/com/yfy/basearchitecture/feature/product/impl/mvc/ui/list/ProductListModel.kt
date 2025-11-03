package com.yfy.basearchitecture.feature.product.impl.mvc.ui.list

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcModel
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.impl.mvc.domain.usecase.GetCategoriesUseCase
import com.yfy.basearchitecture.feature.product.impl.mvc.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ProductListModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val cartRepository: CartRepository
) : BaseMvcModel() {
    
    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()
    
    suspend fun loadProducts(page: Int = 0, categoryId: String? = null) {
        try {
            _state.update { it.copy(isLoading = page == 0, isLoadingMore = page > 0) }
            
            val result = getProductsUseCase(
                GetProductsUseCase.Parameters(
                    page = page,
                    pageSize = 20,
                    categoryId = categoryId
                )
            )
            
            result.collect { products ->
                _state.update { currentState ->
                    currentState.copy(
                        products = if (page == 0) products else currentState.products + products,
                        currentPage = page,
                        hasMore = products.size == 20,
                        isLoading = false,
                        isLoadingMore = false
                    )
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message, isLoading = false, isLoadingMore = false) }
        }
    }
    
    suspend fun loadCategories() {
        try {
            val result = getCategoriesUseCase(Unit)
            result.collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        } catch (e: Exception) {
            // Silent fail for categories
        }
    }
    
    suspend fun loadCartCount() {
        try {
            val result = cartRepository.getCart()
            result.collect { cartItems ->
                _state.update { it.copy(cartItemCount = cartItems.sumOf { item -> item.quantity }) }
            }
        } catch (e: Exception) {
            // Silent fail for cart count
        }
    }
    
    fun updateSelectedCategory(categoryId: String?) {
        _state.update { it.copy(selectedCategoryId = categoryId, products = emptyList(), currentPage = 0) }
    }
}
