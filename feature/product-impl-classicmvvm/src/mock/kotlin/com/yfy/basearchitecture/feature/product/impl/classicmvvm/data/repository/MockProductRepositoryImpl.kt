package com.yfy.basearchitecture.feature.product.impl.classicmvvm.data.repository

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.base.BaseRepository
import com.yfy.basearchitecture.core.ui.api.extensions.getJson
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.api.model.Category
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockProductRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseRepository(), ProductRepository {

    override fun getProducts(page: Int, pageSize: Int, categoryId: String?): Flow<List<Product>> = sendRequest {
        context.getJson<List<Product>>(file = R.raw.products)
            .let { allProducts ->
                var filtered = allProducts
                if (categoryId != null) {
                    filtered = filtered.filter { it.categoryId == categoryId }
                }
                val startIndex = page * pageSize
                val endIndex = minOf(startIndex + pageSize, filtered.size)
                if (startIndex < filtered.size) filtered.subList(startIndex, endIndex) else emptyList()
            }
    }

    override fun getProductDetail(productId: String): Flow<Product> = sendRequest {
        context.getJson<List<Product>>(file = R.raw.products)
            .find { it.id == productId }
            ?: throw Exception("Product not found")
    }

    override fun getCategories(): Flow<List<Category>> = sendRequest {
        context.getJson(file = R.raw.categories)
    }
}
