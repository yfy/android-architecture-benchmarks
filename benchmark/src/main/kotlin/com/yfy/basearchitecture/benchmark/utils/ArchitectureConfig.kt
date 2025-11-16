package com.yfy.basearchitecture.benchmark.utils

import com.yfy.basearchitecture.benchmark.BuildConfig

/**
 * Architecture Configuration for Benchmarks
 *
 * Architecture is automatically read from BuildConfig.CURRENT_ARCHITECTURE
 * which is set in benchmark/build.gradle.kts
 *
 * Available architectures:
 * - CLASSICMVVM: Classic MVVM with ViewModel
 * - SINGLESTATEMVVM: Single-State MVVM
 * - MVC: Model-View-Controller
 * - MVP: Model-View-Presenter
 * - MVI: Model-View-Intent
 */
object ArchitectureConfig {

    val CURRENT_ARCHITECTURE = Architecture.valueOf(
        BuildConfig.CURRENT_ARCHITECTURE.uppercase()
    )

    enum class Architecture(private val moduleName: String, private val displayName: String) {
        CLASSICMVVM("classicmvvm", "Classic MVVM"),
        SINGLESTATEMVVM("singlestatemvvm", "Single-State MVVM"),
        MVC("mvc", "MVC"),
        MVP("mvp", "MVP"),
        MVI("mvi", "MVI");

        fun getShortName(): String = moduleName
        fun getFullName(): String = displayName
    }

    /**
     * Get current architecture info
     */
    fun getCurrentArchitectureInfo(): String {
        return "${CURRENT_ARCHITECTURE.getFullName()} (${CURRENT_ARCHITECTURE.getShortName()})"
    }
}