package com.yfy.basearchitecture.feature.cart.impl.mvi.di

import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.cart.api.CheckoutRepository
import com.yfy.basearchitecture.feature.cart.impl.mvi.data.repository.DevCartRepositoryImpl
import com.yfy.basearchitecture.feature.cart.impl.mvi.data.repository.DevCheckoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    @Singleton
    abstract fun provideCartRepository(
        impl: DevCartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun provideCheckoutRepository(
        impl: DevCheckoutRepositoryImpl
    ): CheckoutRepository
}
