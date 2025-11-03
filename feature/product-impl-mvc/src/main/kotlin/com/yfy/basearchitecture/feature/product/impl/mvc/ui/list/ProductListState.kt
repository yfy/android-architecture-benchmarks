package com.yfy.basearchitecture.feature.product.impl.mvc.ui.list

import com.yfy.basearchitecture.feature.product.api.model.Category
import com.yfy.basearchitecture.feature.product.api.model.Product

data class ProductListState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val cartItemCount: Int = 0
)
