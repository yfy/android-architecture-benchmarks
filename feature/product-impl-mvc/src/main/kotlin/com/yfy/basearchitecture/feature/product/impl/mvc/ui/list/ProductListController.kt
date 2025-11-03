package com.yfy.basearchitecture.feature.product.impl.mvc.ui.list

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcController
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductListController @Inject constructor(
    model: ProductListModel,
    private val navigation: ProductNavigation
) : BaseMvcController<ProductListModel>(model) {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        scope.launch {
            model.loadCategories()
            model.loadProducts()
            model.loadCartCount()
        }
    }
    
    fun onProductClicked(productId: String) {
        navigation.navigateToProductDetail(productId)
    }
    
    fun onCategorySelected(categoryId: String) {
        val newCategoryId = if (model.state.value.selectedCategoryId == categoryId) null else categoryId
        model.updateSelectedCategory(newCategoryId)
        scope.launch {
            model.loadProducts(page = 0, categoryId = newCategoryId)
        }
    }
    
    fun onLoadMore() {
        if (!model.state.value.isLoadingMore && model.state.value.hasMore) {
            scope.launch {
                model.loadProducts(
                    page = model.state.value.currentPage + 1,
                    categoryId = model.state.value.selectedCategoryId
                )
            }
        }
    }
    
    fun onRefresh() {
        model.updateSelectedCategory(model.state.value.selectedCategoryId)
        scope.launch {
            model.loadProducts(page = 0, categoryId = model.state.value.selectedCategoryId)
        }
    }
    
    fun onCartClick() {
        navigation.navigateToCart()
    }

    fun onChatClick() {
        navigation.navigateToChat()
    }
    
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
