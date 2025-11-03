package com.yfy.basearchitecture.feature.product.impl.mvi.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetProductDetailUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : BaseFlowUseCase<String, Product>() {
    
    override fun execute(parameters: String): Flow<Product> {
        return productRepository.getProductDetail(parameters)
    }
}
