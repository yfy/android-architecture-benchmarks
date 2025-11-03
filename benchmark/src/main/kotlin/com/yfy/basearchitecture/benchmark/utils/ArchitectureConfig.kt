package com.yfy.basearchitecture.benchmark.utils
/**
 * Architecture Configuration for Benchmarks
 *
 * IMPORTANT: Change this value before running benchmarks for different architectures
 *
 * Available architectures:
 * - CLASSIC_MVVM: Classic MVVM with ViewModel
 * - SINGLE_STATE_MVVM: Single-State MVVM
 * - MVC: Model-View-Controller
 * - MVP: Model-View-Presenter
 * - MVI: Model-View-Intent
 */
object ArchitectureConfig {

    val CURRENT_ARCHITECTURE = Architecture.MVP

    enum class Architecture(private val moduleName: String, private val displayName: String) {
        CLASSIC_MVVM("classicmvvm", "Classic MVVM"),
        SINGLE_STATE_MVVM("singlestatemvvm", "Single-State MVVM"),
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