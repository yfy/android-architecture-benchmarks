package com.yfy.basearchitecture.feature.product.impl.classicmvvm.domain.usecase

import com.yfy.basearchitecture.core.ui.api.base.BaseFlowUseCase
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.api.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCategoriesUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : BaseFlowUseCase<Unit, List<Category>>() {
    
    override fun execute(parameters: Unit): Flow<List<Category>> {
        return productRepository.getCategories()
    }
}
