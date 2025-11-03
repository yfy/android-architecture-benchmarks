package com.yfy.basearchitecture.feature.cart.impl.di

import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import com.yfy.basearchitecture.feature.cart.api.CartNavigation
import com.yfy.basearchitecture.feature.cart.api.CartRepository
import com.yfy.basearchitecture.feature.cart.api.CheckoutRepository
import com.yfy.basearchitecture.feature.cart.impl.data.local.CartSessionManager
import com.yfy.basearchitecture.feature.cart.impl.data.repository.MockCartRepositoryImpl
import com.yfy.basearchitecture.feature.cart.impl.data.repository.MockCheckoutRepositoryImpl
import com.yfy.basearchitecture.feature.cart.impl.navigation.CartNavGraphBuilder
import com.yfy.basearchitecture.feature.cart.impl.navigation.CartNavigationImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @Singleton
    fun provideCartRepository(impl: MockCartRepositoryImpl): CartRepository = impl

    @Provides
    @Singleton
    fun provideCheckoutRepository(impl: MockCheckoutRepositoryImpl): CheckoutRepository = impl

    @Provides
    @Singleton
    fun provideCartNavigation(impl: CartNavigationImpl): CartNavigation = impl

    @Provides
    @IntoSet
    fun provideFeatureNavGraphBuilder(builder: CartNavGraphBuilder): FeatureNavGraphBuilder = builder
}
