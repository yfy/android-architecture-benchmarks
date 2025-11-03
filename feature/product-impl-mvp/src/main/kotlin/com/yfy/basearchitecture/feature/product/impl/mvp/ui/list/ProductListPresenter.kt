package com.yfy.basearchitecture.feature.product.impl.mvp.ui.list

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.base.BasePresenterImpl
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.impl.mvp.domain.usecase.GetCategoriesUseCase
import com.yfy.basearchitecture.feature.product.impl.mvp.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductListPresenter @Inject constructor(
    navigationManager: NavigationManager,
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val cartRepository: CartRepository,
    private val navigation: ProductNavigation
) : BasePresenterImpl(navigationManager) {
    private var view: ProductListView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    var currentPage = 0
    private var selectedCategoryId: String? = null
    private var hasMore = true
    private var cachedProducts = mutableListOf<com.yfy.basearchitecture.feature.product.api.model.Product>()
    private var cachedCategories = mutableListOf<com.yfy.basearchitecture.feature.product.api.model.Category>()
    private var cachedCartCount = 0
    
    fun attachView(view: ProductListView) {
        this.view = view
        if (cachedProducts.isNotEmpty()) {
            view.showProducts(cachedProducts)
        }
        if (cachedCategories.isNotEmpty()) {
            view.showCategories(cachedCategories)
        }
        view.showCartItemCount(cachedCartCount)
        
        if (cachedProducts.isEmpty()) {
            loadInitialData()
        }
    }
    
    fun detachView() {
        view = null
    }
    
    override fun onViewCreated() {
        super.onViewCreated()
        logScreen("ProductListScreen")
    }
    
    override fun onViewDestroyed() {
        super.onViewDestroyed()
        detachView()
    }
    
    private fun loadInitialData() {
        loadCategories()
        loadProducts()
        loadCartCount()
    }
    
    private fun loadProducts(page: Int = 0) {
        scope.launch {
            try {
                if (page == 0) {
                    setLoading(true)
                    view?.showLoading()
                } else {
                    view?.showLoadingMore()
                }
                
                getProductsUseCase(
                    GetProductsUseCase.Parameters(
                        page = page,
                        pageSize = 20,
                        categoryId = selectedCategoryId
                    )
                )
                    .catch { error ->
                        view?.showError(error.localizedMessage ?: "Unknown error")
                    }
                    .collect { products ->
                        if (page == 0) {
                            cachedProducts.clear()
                            cachedProducts.addAll(products)
                        } else {
                            cachedProducts.addAll(products)
                        }
                        view?.showProducts(cachedProducts)
                        currentPage = page
                        hasMore = products.size == 20
                    }
            } finally {
                if (page == 0) {
                    setLoading(false)
                    view?.hideLoading()
                } else {
                    view?.hideLoadingMore()
                }
            }
        }
    }
    
    private fun loadCategories() {
        scope.launch {
            try {
                getCategoriesUseCase(Unit)
                    .catch { /* Silent fail for categories */ }
                    .collect { categories ->
                        cachedCategories.clear()
                        cachedCategories.addAll(categories)
                        view?.showCategories(categories)
                    }
            } catch (e: Exception) {
                // Silent fail for categories
            }
        }
    }
    
    private fun loadCartCount() {
        scope.launch {
            try {
                cartRepository.getCart()
                    .catch { }
                    .collect { cartItems ->
                        val count = cartItems.sumOf { item -> item.quantity }
                        cachedCartCount = count
                        view?.showCartItemCount(count)
                    }
            } catch (e: Exception) {
                // Silent fail for cart count
            }
        }
    }
    
    fun onProductClicked(productId: String) {
        navigation.navigateToProductDetail(productId)
    }
    
    fun onCategorySelected(categoryId: String) {
        selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
        currentPage = 0
        loadProducts(page = 0)
    }
    
    fun onLoadMore() {
        if (hasMore) {
            loadProducts(page = currentPage + 1)
        }
    }
    
    fun onRefresh() {
        currentPage = 0
        loadProducts(page = 0)
    }
    
    fun onCartClick() {
        navigation.navigateToCart()
    }

    fun onChatClick() {
        navigation.navigateToChat()
    }

    fun navigateBack() {
        navigation.navigateBack()
    }
    
    fun refreshCartCount() {
        loadCartCount()
    }
}
