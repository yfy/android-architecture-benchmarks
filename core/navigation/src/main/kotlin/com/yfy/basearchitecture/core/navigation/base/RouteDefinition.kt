package com.yfy.basearchitecture.core.navigation.base

data class RouteDefinition(
    val routePattern: String,
    val routeBuilder: (String) -> String = { it }
)