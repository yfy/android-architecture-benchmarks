package com.yfy.basearchitecture.feature.product.impl.classicmvvm.di

import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.data.repository.DevProductRepositoryImpl
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.navigation.ProductNavGraphBuilder
import com.yfy.basearchitecture.feature.product.impl.classicmvvm.navigation.ProductNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        devProductRepositoryImpl: DevProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindProductNavigation(
        productNavigationImpl: ProductNavigationImpl
    ): ProductNavigation

    companion object {
        @Provides
        @IntoSet
        fun provideProductNavGraphBuilder(
            productNavGraphBuilder: ProductNavGraphBuilder
        ): FeatureNavGraphBuilder = productNavGraphBuilder
    }
}
