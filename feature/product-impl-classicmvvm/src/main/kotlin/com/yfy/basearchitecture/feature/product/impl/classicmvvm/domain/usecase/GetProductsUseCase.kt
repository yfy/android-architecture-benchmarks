package com.yfy.basearchitecture.feature.product.impl.classicmvvm.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : BaseFlowUseCase<GetProductsUseCase.Parameters, List<Product>>() {
    
    data class Parameters(
        val page: Int = 0,
        val pageSize: Int = 20,
        val categoryId: String? = null
    )
    
    override fun execute(parameters: Parameters): Flow<List<Product>> {
        return productRepository.getProducts(
            page = parameters.page,
            pageSize = parameters.pageSize,
            categoryId = parameters.categoryId
        )
    }
}
