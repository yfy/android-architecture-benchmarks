package com.yfy.basearchitecture.di

import com.yfy.basearchitecture.core.navigation.base.FeatureNavGraphBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRouteRegistry @Inject constructor(
    private val builders: Set<@JvmSuppressWildcards FeatureNavGraphBuilder>
) {
    fun getNavGraphBuilders(): Set<FeatureNavGraphBuilder> = builders
}