package com.yfy.basearchitecture.feature.product.api

import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.api.model.Category
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(page: Int = 0, pageSize: Int = 20, categoryId: String? = null): Flow<List<Product>>
    fun getProductDetail(productId: String): Flow<Product>
    fun getCategories(): Flow<List<Category>>
}
