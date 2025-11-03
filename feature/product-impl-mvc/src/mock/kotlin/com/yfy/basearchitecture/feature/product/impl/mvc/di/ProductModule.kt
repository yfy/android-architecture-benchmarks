package com.yfy.basearchitecture.feature.product.impl.mvc.di

import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.product.api.ProductNavigation
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.impl.mvc.navigation.ProductNavGraphBuilder
import com.yfy.basearchitecture.feature.product.impl.mvc.navigation.ProductNavigationImpl
import com.yfy.basearchitecture.feature.product.impl.mvc.repository.MockProductRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {

    @Provides
    @Singleton
    fun provideProductRepository(impl: MockProductRepositoryImpl): ProductRepository = impl

    @Provides
    @Singleton
    fun provideProductNavigation(impl: ProductNavigationImpl): ProductNavigation = impl

    @Provides
    @IntoSet
    fun provideFeatureNavGraphBuilder(builder: ProductNavGraphBuilder): FeatureNavGraphBuilder = builder
}
