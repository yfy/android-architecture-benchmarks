package com.yfy.basearchitecture.benchmark.model

import com.yfy.basearchitecture.benchmark.utils.ArchitectureConfig

/**
 * Base metric interface
 */
sealed class TestMetrics(
    open val testName: String,
    open val architecture: String,
    open val timestamp: Long
)

/**
 * Startup test metrics
 */
data class StartupMetrics(
    override val testName: String,
    override val architecture: String = ArchitectureConfig.CURRENT_ARCHITECTURE.getShortName(),
    override val timestamp: Long = System.currentTimeMillis(),
    val iterations: Int,
    val startupMode: String // "COLD" or "WARM"
) : TestMetrics(testName, architecture, timestamp)

/**
 * Frame timing / rendering test metrics
 */
data class FrameTimingMetrics(
    override val testName: String,
    override val architecture: String = ArchitectureConfig.CURRENT_ARCHITECTURE.getShortName(),
    override val timestamp: Long = System.currentTimeMillis(),
    val totalDurationMs: Long,
    val actionCount: Int,
    val actionType: String, // "scroll", "click", "fling", etc.
    val additionalInfo: Map<String, String> = emptyMap()
) : TestMetrics(testName, architecture, timestamp)

/**
 * Memory test metrics
 */
data class MemorySnapshot(
    val label: String,
    val timestamp: Long,

    // Process memory (from ActivityManager) - MB
    val totalPss: Double,
    val totalPrivateDirty: Double,
    val totalSharedDirty: Double,
    val nativeHeap: Double,
    val dalvikHeap: Double,
    val otherPss: Double = 0.0, // Optional, default 0

    // Runtime memory (from Runtime) - MB
    val usedMemoryMB: Double = 0.0,
    val totalMemoryMB: Double = 0.0,
    val maxMemoryMB: Double = 0.0,
    val freeMemoryMB: Double = 0.0,

    // Calculated
    val memoryPressurePercent: Double = 0.0
)

data class MemoryMetrics(
    override val testName: String,
    override val architecture: String = ArchitectureConfig.CURRENT_ARCHITECTURE.getShortName(),
    override val timestamp: Long = System.currentTimeMillis(),
    val snapshots: List<MemorySnapshot>
) : TestMetrics(testName, architecture, timestamp)

/**
 * Interaction/latency test metrics
 */
data class InteractionMetrics(
    override val testName: String,
    override val architecture: String = ArchitectureConfig.CURRENT_ARCHITECTURE.getShortName(),
    override val timestamp: Long = System.currentTimeMillis(),
    val latencies: List<Long>,
    val interactionType: String // "button_click", "state_update", etc.
) : TestMetrics(testName, architecture, timestamp)