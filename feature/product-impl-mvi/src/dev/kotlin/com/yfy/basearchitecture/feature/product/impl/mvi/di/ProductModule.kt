package com.yfy.basearchitecture.feature.product.impl.mvi.di

import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.impl.mvi.data.repository.DevProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {

    @Binds
    @Singleton
    abstract fun provideProductRepository(
        impl: DevProductRepositoryImpl
    ): ProductRepository
}
