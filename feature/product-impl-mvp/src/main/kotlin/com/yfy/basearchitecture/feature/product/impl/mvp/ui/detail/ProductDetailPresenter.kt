package com.yfy.basearchitecture.feature.product.impl.mvp.ui.detail

import android.content.Context
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.base.BasePresenterImpl
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.mvp.domain.usecase.GetProductDetailUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductDetailPresenter @Inject constructor(
    navigationManager: NavigationManager,
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val navigation: ProductNavigation,
    private val cartRepository: CartRepository,
    @ApplicationContext private val context: Context
) : BasePresenterImpl(navigationManager) {
    private var view: ProductDetailView? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var quantity = 1
    
    fun attachView(view: ProductDetailView, productId: String) {
        this.view = view
        loadProduct(productId)
    }
    
    fun detachView() {
        view = null
        scope.cancel()
    }
    
    override fun onViewCreated() {
        super.onViewCreated()
        logScreen("ProductDetailScreen")
    }
    
    override fun onViewDestroyed() {
        super.onViewDestroyed()
        detachView()
    }
    
    private fun loadProduct(productId: String) {
        scope.launch {
            try {
                setLoading(true)
                view?.showLoading()
                
                getProductDetailUseCase(productId)
                    .catch { error ->
                        view?.showError(error.localizedMessage ?: "Unknown error")
                    }
                    .collect { product ->
                        view?.showProduct(product)
                    }
            } finally {
                setLoading(false)
                view?.hideLoading()
            }
        }
    }
    
    fun onQuantityChange(newQuantity: Int) {
        quantity = newQuantity
    }
    
    fun addToCart(product: Product, quantity: Int) {
        scope.launch {
            try {
                cartRepository.addToCart(product, quantity)
                    .catch { error ->
                        view?.showError(error.localizedMessage ?: "Failed to add to cart")
                    }
                    .collect{
                        showToast(context, "$quantity sepete eklendi")
                    }
            } catch (e: Exception) {
                view?.showError("Failed to add to cart")
            }
        }
    }
    
    fun navigateBack() {
        navigation.navigateBack()
    }
}
