package com.yfy.basearchitecture.core.navigation.base

import androidx.navigation.NavGraphBuilder
import com.yfy.basearchitecture.core.navigation.NavigationManager

interface FeatureNavGraphBuilder {
    fun NavGraphBuilder.buildNavGraph(navManager: NavigationManager)
}