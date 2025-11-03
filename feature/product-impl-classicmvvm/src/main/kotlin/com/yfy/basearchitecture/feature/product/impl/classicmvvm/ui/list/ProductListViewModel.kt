package com.yfy.basearchitecture.feature.product.impl.classicmvvm.ui.list

import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import com.yfy.basearchitecture.core.ui.api.extensions.serviceLaunch
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.model.Category
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.domain.usecase.GetCategoriesUseCase
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val cartRepository: CartRepository,
    private val navigation: ProductNavigation
) : BaseComposeViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private var currentPage = 0
    private var hasMore = true

    init {
        loadCategories()
        loadCartCount()
        loadProducts()
    }

    private fun loadProducts(page: Int = 0) {
        serviceLaunch {
            getProductsUseCase(
                GetProductsUseCase.Parameters(
                    page = page,
                    pageSize = 20,
                    categoryId = _selectedCategoryId.value
                )
            )
                .catch { error ->
                    handleError(error)
                    _error.value = error.localizedMessage
                    setLoading(false)
                    _isLoadingMore.value = false
                }
                .collect { products ->
                    _products.value = if (page == 0) {
                        products
                    } else {
                        _products.value + products
                    }
                    currentPage = page
                    hasMore = products.size == 20
                    setLoading(false)
                    _isLoadingMore.value = false
                    _error.value = null
                }
        }
    }

    private fun loadCategories() {
        serviceLaunch {
            getCategoriesUseCase(Unit)
                .catch { handleError(it) }
                .collect { categories ->
                    _categories.value = categories
                }
        }
    }

    fun onProductClick(productId: String) {
        navigation.navigateToProductDetail(productId)
    }

    fun onCategorySelected(categoryId: String) {
        val newCategoryId = if (_selectedCategoryId.value == categoryId) null else categoryId
        _selectedCategoryId.value = newCategoryId
        _products.value = emptyList()
        currentPage = 0
        loadProducts()
    }

    fun loadMoreProducts() {
        if (!_isLoadingMore.value && hasMore) {
            _isLoadingMore.value = true
            loadProducts(page = currentPage + 1)
        }
    }

    fun refresh() {
        _products.value = emptyList()
        currentPage = 0
        loadProducts()
    }

    private fun loadCartCount() {
        serviceLaunch {
            cartRepository.getCart()
                .catch { }
                .collect { cartItems ->
                    _cartItemCount.value = cartItems.sumOf { item -> item.quantity }
                }
        }
    }

    fun onCartClick() {
        navigation.navigateToCart()
    }

    fun onChatClick() {
        navigation.navigateToChat()
    }
}
