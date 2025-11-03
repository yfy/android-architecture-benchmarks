package com.yfy.basearchitecture.feature.product.impl.mvc.ui.detail

import com.yfy.basearchitecture.feature.product.api.model.Product

data class ProductDetailState(
    val product: Product? = null,
    val quantity: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null
)
