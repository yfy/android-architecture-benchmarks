package com.yfy.basearchitecture.core.navigation.di

import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.navigation.NavigationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    abstract fun bindNavigationManager(
        navigationManagerImpl: NavigationManagerImpl
    ): NavigationManager

}