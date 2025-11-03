package com.yfy.basearchitecture.benchmark.utils

import android.annotation.SuppressLint
import androidx.test.platform.app.InstrumentationRegistry
import com.yfy.basearchitecture.benchmark.model.FrameTimingMetrics
import com.yfy.basearchitecture.benchmark.model.InteractionMetrics
import com.yfy.basearchitecture.benchmark.model.MemoryMetrics
import com.yfy.basearchitecture.benchmark.model.StartupMetrics
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BenchmarkHelper {
    const val PACKAGE_NAME = "com.yfy.basearchitecture.mock"

    // Startup Tests
    const val STARTUP_ITERATIONS = 15

    // Scroll/Interaction Tets
    const val SCROLL_ITERATIONS = 5

    // Jank Tests
    const val JANK_ITERATIONS = 5

    // Memory Tests
    const val MEMORY_ITERATIONS = 3

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private val architecture = ArchitectureConfig.CURRENT_ARCHITECTURE.getShortName()

    /**
     * Get results directory for current architecture
     */
    private fun getResultsDir(): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Use app-specific external directory (Android 11+ compatible)
        val externalDir = context.getExternalFilesDir(null)
            ?: context.filesDir

        val baseDir = File(externalDir, "benchmark_results/$architecture")

        if (!baseDir.exists()) {
            val created = baseDir.mkdirs()
            println("Creating directory: ${baseDir.absolutePath} - Success: $created")
        }

        return baseDir
    }

    fun exportStartupResults(metrics: StartupMetrics) {
        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date(metrics.timestamp))
        val file = File(resultsDir, "startup_${metrics.testName}_${timestamp}.txt")

        file.printWriter().use { out ->
            out.println("=" * 50)
            out.println("STARTUP TEST RESULTS")
            out.println("=" * 50)
            out.println()
            out.println("Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
            out.println("Test Name: ${metrics.testName}")
            out.println("Startup Mode: ${metrics.startupMode}")
            out.println("Iterations: ${metrics.iterations}")
            out.println("Timestamp: $timestamp")
            out.println("Package: $PACKAGE_NAME")
            out.println()
            out.println("-" * 50)
            out.println()
            out.println("IMPORTANT:")
            out.println("Detailed timing metrics (timeToInitialDisplay, timeToFullDisplay)")
            out.println("are captured by StartupTimingMetric and saved in JSON format.")
            out.println()
            out.println("JSON location:")
            out.println("  benchmark/build/outputs/androidTest-results/")
            out.println()
            out.println("To analyze:")
            out.println("  1. Open JSON file in above directory")
            out.println("  2. Look for 'startupMs' metrics")
            out.println("  3. Compare across architectures")
            out.println()
            out.println("=" * 50)
        }

        logSuccess("Startup", metrics.testName, file)
    }

    // ============================================
    // FRAME TIMING / RENDERING BENCHMARKS
    // ============================================

    /**
     * Export frame timing / rendering test results
     */
    fun exportFrameTimingResults(metrics: FrameTimingMetrics) {
        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date(metrics.timestamp))
        val file = File(resultsDir, "frametiming_${metrics.testName}_${timestamp}.csv")

        file.printWriter().use { out ->
            // Header
            out.println("# Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
            out.println("# Test: ${metrics.testName}")
            out.println("# Timestamp: $timestamp")
            out.println("#")

            // Test execution details
            out.println("# Test Execution Details")
            out.println("Metric,Value")
            out.println("Architecture,$architecture")
            out.println("Test_Name,${metrics.testName}")
            out.println("Total_Duration_MS,${metrics.totalDurationMs}")
            out.println("Action_Type,${metrics.actionType}")
            out.println("Action_Count,${metrics.actionCount}")
            out.println("Avg_Action_Duration_MS,${if (metrics.actionCount > 0) metrics.totalDurationMs / metrics.actionCount else 0}")

            // Additional info
            if (metrics.additionalInfo.isNotEmpty()) {
                out.println()
                out.println("# Additional Info")
                metrics.additionalInfo.forEach { (key, value) ->
                    out.println("$key,$value")
                }
            }

            out.println()
            out.println("# Frame Timing Metrics (P50, P90, P95, P99)")
            out.println("# These are captured by FrameTimingMetric and saved in JSON")
            out.println("# JSON location: benchmark/build/outputs/androidTest-results/")
            out.println("#")
            out.println("# Jank Threshold: > 16.67ms (60 FPS)")
            out.println("# Good: P95 < 16.67ms")
            out.println("# Poor: P95 > 16.67ms or P99 > 20ms")
        }

        logSuccess("Frame Timing", metrics.testName, file)
    }

    // ============================================
    // MEMORY BENCHMARKS
    // ============================================

    /**
     * Export memory test results with snapshots
     */
    @SuppressLint("DefaultLocale")
    fun exportMemoryResults(metrics: MemoryMetrics) {
        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date(metrics.timestamp))
        val file = File(resultsDir, "memory_${metrics.testName}_${timestamp}.csv")

        file.printWriter().use { out ->
            // Header
            out.println("# Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
            out.println("# Test: ${metrics.testName}")
            out.println("# Timestamp: $timestamp")
            out.println("# Snapshots: ${metrics.snapshots.size}")
            out.println("#")

            // CSV Header
            out.println("Label,Timestamp,TotalPSS_MB,PrivateDirty_MB,SharedDirty_MB,NativeHeap_MB,DalvikHeap_MB,UsedMemory_MB,TotalMemory_MB,MaxMemory_MB,FreeMemory_MB,MemoryPressure_%")

            // Data rows
            metrics.snapshots.forEach { snapshot ->
                out.println(
                    "${snapshot.label}," +
                            "${snapshot.timestamp}," +
                            "${String.format("%.2f", snapshot.totalPss)}," +
                            "${String.format("%.2f", snapshot.totalPrivateDirty)}," +
                            "${String.format("%.2f", snapshot.totalSharedDirty)}," +
                            "${String.format("%.2f", snapshot.nativeHeap)}," +
                            "${String.format("%.2f", snapshot.dalvikHeap)}," +
                            "${String.format("%.2f", snapshot.usedMemoryMB)}," +
                            "${String.format("%.2f", snapshot.totalMemoryMB)}," +
                            "${String.format("%.2f", snapshot.maxMemoryMB)}," +
                            "${String.format("%.2f", snapshot.freeMemoryMB)}," +
                            "${String.format("%.2f", snapshot.memoryPressurePercent)}"
                )
            }

            // Summary
            if (metrics.snapshots.isNotEmpty()) {
                out.println()
                out.println("# Summary Statistics")
                val initial = metrics.snapshots.first()
                val peak = metrics.snapshots.maxByOrNull { it.totalPss }
                val final = metrics.snapshots.last()

                out.println("# Initial_PSS_MB: ${String.format("%.2f", initial.totalPss)}")
                out.println(
                    "# Peak_PSS_MB: ${
                        String.format(
                            "%.2f",
                            peak?.totalPss ?: 0.0
                        )
                    } (${peak?.label})"
                )
                out.println("# Final_PSS_MB: ${String.format("%.2f", final.totalPss)}")
                out.println(
                    "# Growth_MB: ${
                        String.format(
                            "%.2f",
                            final.totalPss - initial.totalPss
                        )
                    }"
                )

                // For comparison spreadsheet
                out.println()
                out.println("# Comparison Row (copy to spreadsheet)")
                out.println("# Architecture,Initial_MB,Peak_MB,Final_MB,Growth_MB")
                out.println(
                    "# $architecture," +
                            "${String.format("%.2f", initial.totalPss)}," +
                            "${String.format("%.2f", peak?.totalPss ?: 0.0)}," +
                            "${String.format("%.2f", final.totalPss)}," +
                            "${String.format("%.2f", final.totalPss - initial.totalPss)}"
                )
            }
        }

        logSuccess("Memory", metrics.testName, file)
        println("   Snapshots: ${metrics.snapshots.size}")
    }

    // ============================================
    // INTERACTION / LATENCY BENCHMARKS
    // ============================================

    /**
     * Export interaction/latency test results
     */
    fun exportInteractionResults(metrics: InteractionMetrics) {
        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date(metrics.timestamp))
        val file = File(resultsDir, "interaction_${metrics.testName}_${timestamp}.csv")

        file.printWriter().use { out ->
            // Header
            out.println("# Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
            out.println("# Test: ${metrics.testName}")
            out.println("# Interaction Type: ${metrics.interactionType}")
            out.println("# Timestamp: $timestamp")
            out.println("# Samples: ${metrics.latencies.size}")
            out.println("#")

            // CSV Header
            out.println("Iteration,Latency_MS")

            // Data rows
            metrics.latencies.forEachIndexed { index, latency ->
                out.println("$index,$latency")
            }

            // Statistics
            if (metrics.latencies.isNotEmpty()) {
                val sorted = metrics.latencies.sorted()
                out.println()
                out.println("# Statistics")
                out.println("Metric,Value_MS")
                out.println("Count,${metrics.latencies.size}")
                out.println("Mean,${String.format("%.2f", metrics.latencies.average())}")
                out.println("Median,${sorted[sorted.size / 2]}")
                out.println("Min,${sorted.first()}")
                out.println("Max,${sorted.last()}")
                out.println("P90,${sorted[(sorted.size * 0.9).toInt()]}")
                out.println("P95,${sorted[(sorted.size * 0.95).toInt()]}")
                out.println("P99,${sorted[(sorted.size * 0.99).toInt()]}")

                // For comparison
                out.println()
                out.println("# Comparison Row (copy to spreadsheet)")
                out.println("# Architecture,Interaction,Mean,P50,P90,P95,P99")
                out.println(
                    "# $architecture," +
                            "${metrics.interactionType}," +
                            "${String.format("%.2f", metrics.latencies.average())}," +
                            "${sorted[sorted.size / 2]}," +
                            "${sorted[(sorted.size * 0.9).toInt()]}," +
                            "${sorted[(sorted.size * 0.95).toInt()]}," +
                            "${sorted[(sorted.size * 0.99).toInt()]}"
                )
            }
        }

        logSuccess("Interaction", metrics.testName, file)
        println("   Samples: ${metrics.latencies.size}")
    }

    // ============================================
    // HELPERS
    // ============================================

    private fun logSuccess(type: String, testName: String, file: File) {
        println("✅ $type test: $testName")
        println("   Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
        println("   Results: ${file.absolutePath}")
    }

    /**
     * Create session summary
     */
    fun createSessionSummary() {
        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date())
        val file = File(resultsDir, "SESSION_SUMMARY_${timestamp}.txt")

        file.printWriter().use { out ->
            out.println("=" * 60)
            out.println("BENCHMARK SESSION SUMMARY")
            out.println("=" * 60)
            out.println()
            out.println("Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
            out.println(
                "Session Date: ${
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(
                        Date()
                    )
                }"
            )
            out.println("Package: $PACKAGE_NAME")
            out.println()
            out.println("Results Directory:")
            out.println("  ${resultsDir.absolutePath}")
            out.println()
            out.println("Files Generated:")
            resultsDir.listFiles()
                ?.sortedBy { it.name }
                ?.forEach { out.println("  - ${it.name}") }
            out.println()
            out.println("Next Steps:")
            out.println("  1. Review individual test results above")
            out.println("  2. Check JSON metrics in: benchmark/build/outputs/androidTest-results/")
            out.println("  3. Compare results across architectures")
            out.println("  4. Run: adb pull /sdcard/benchmark_results/ ./local_results/")
            out.println()
            out.println("=" * 60)
        }

        println("\n📊 Session summary created: ${file.absolutePath}")
    }

    private operator fun String.times(n: Int) = repeat(n)
}
