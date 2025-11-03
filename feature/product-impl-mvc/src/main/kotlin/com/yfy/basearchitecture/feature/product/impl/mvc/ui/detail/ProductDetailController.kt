package com.yfy.basearchitecture.feature.product.impl.mvc.ui.detail

import com.yfy.basearchitecture.core.ui.base.mvc.BaseMvcController
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductDetailController @Inject constructor(
    model: ProductDetailModel,
    private val navigation: ProductNavigation,
    productId: String
) : BaseMvcController<ProductDetailModel>(model) {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        scope.launch { model.loadProduct(productId) }
    }
    
    fun onQuantityChange(quantity: Int) {
        model.updateQuantity(quantity)
    }
    
    fun onAddToCart(product: Product, quantity: Int) {
        scope.launch {
            model.addToCart(product, quantity)
        }
    }
    
    fun onBackClick() {
        navigation.navigateBack()
    }
    
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
