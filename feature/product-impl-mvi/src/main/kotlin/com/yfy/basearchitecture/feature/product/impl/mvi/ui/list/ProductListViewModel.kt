package com.yfy.basearchitecture.feature.product.impl.mvi.ui.list

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.impl.mvi.domain.usecase.GetCategoriesUseCase
import com.yfy.basearchitecture.feature.product.impl.mvi.domain.usecase.GetProductsUseCase
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
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val cartRepository: CartRepository,
    private val navigation: ProductNavigation
) : BaseComposeViewModel() {

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    init {
        handleIntent(ProductListIntent.LoadProducts)
        loadCategories()
        loadCartCount()
    }

    fun handleIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.LoadProducts -> loadProducts()
            is ProductListIntent.LoadMore -> loadMoreProducts()
            is ProductListIntent.Refresh -> refresh()
            is ProductListIntent.CategorySelected -> onCategorySelected(intent.categoryId)
            is ProductListIntent.ProductClicked -> navigation.navigateToProductDetail(intent.productId)
            is ProductListIntent.CartClicked -> navigation.navigateToCart()
            is ProductListIntent.ChatClicked -> navigation.navigateToChat()
        }
    }

    private fun loadProducts(page: Int = 0) {
        serviceLaunch {
            getProductsUseCase(
                GetProductsUseCase.Parameters(
                    page = page,
                    pageSize = 20,
                    categoryId = _state.value.selectedCategoryId
                )
            )
                .onStart { 
                    if (page == 0) setLoading(true) 
                    else _state.update { it.copy(isLoading = true, isLoadingMore = true) }
                }
                .onCompletion { 
                    setLoading(false)
                    _state.update { it.copy(isLoading = false, isLoadingMore = false) }
                }
                .catch { error ->
                    handleError(error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = error.localizedMessage
                        )
                    }
                }
                .collect { products ->
                    _state.update { currentState ->
                        currentState.copy(
                            products = if (page == 0) products else currentState.products + products,
                            currentPage = page,
                            hasMore = products.size == 20
                        )
                    }
                }
        }
    }

    private fun loadCategories() {
        serviceLaunch {
            getCategoriesUseCase(Unit)
                .catch { handleError(it) }
                .collect { categories ->
                    _state.update { it.copy(categories = categories) }
                }
        }
    }

    private fun loadMoreProducts() {
        if (!_state.value.isLoadingMore && _state.value.hasMore) {
            loadProducts(page = _state.value.currentPage + 1)
        }
    }

    private fun refresh() {
        _state.update { it.copy(products = emptyList(), currentPage = 0) }
        loadProducts()
    }

    private fun onCategorySelected(categoryId: String) {
        _state.update { 
            it.copy(
                selectedCategoryId = if (it.selectedCategoryId == categoryId) null else categoryId,
                products = emptyList(),
                currentPage = 0
            ) 
        }
        loadProducts()
    }

    private fun loadCartCount() {
        serviceLaunch {
            cartRepository.getCart()
                .catch { }
                .collect { cartItems ->
                    _state.update { it.copy(cartItemCount = cartItems.sumOf { item -> item.quantity }) }
                }
        }
    }
}
