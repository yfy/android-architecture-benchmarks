package com.yfy.basearchitecture.core.navigation.base

abstract class FeatureRouteProvider {
    abstract val featurePrefix: String
    abstract fun getRoutes(): List<RouteDefinition>
}