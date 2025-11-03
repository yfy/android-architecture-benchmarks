package com.yfy.basearchitecture.benchmark


import com.yfy.basearchitecture.benchmark.interaction.StateUpdateBenchmark
import com.yfy.basearchitecture.benchmark.memory.MemoryUsageBenchmark
import com.yfy.basearchitecture.benchmark.rendering.CartInteractionBenchmark
import com.yfy.basearchitecture.benchmark.rendering.ChatStreamBenchmark
import com.yfy.basearchitecture.benchmark.rendering.JankMetricBenchmark
import com.yfy.basearchitecture.benchmark.rendering.ProductListScrollBenchmark
import com.yfy.basearchitecture.benchmark.startup.StartupBenchmark
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import org.junit.AfterClass
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Benchmark Test Suite
 *
 * Running this test class will automatically execute all benchmark categories:
 * - Startup
 * - Rendering
 * - Interaction
 * - Memory
 *
 * This suite runs every benchmark sequentially and generates a consolidated summary
 * report at the end via BenchmarkHelper.createSessionSummary().
 *
 * To execute all benchmarks, simply run this class — no additional setup required.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // Startup
    StartupBenchmark::class,

    // Rendering
    ProductListScrollBenchmark::class,
    CartInteractionBenchmark::class,
    ChatStreamBenchmark::class,
    JankMetricBenchmark::class,

    // Interaction
    StateUpdateBenchmark::class,

    // Memory
    MemoryUsageBenchmark::class
)
class BenchmarkTestSuite {
    companion object {
        @AfterClass
        @JvmStatic
        fun generateSummary() {
            BenchmarkHelper.createSessionSummary()
        }
    }
}