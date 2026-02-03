package com.yfy.basearchitecture.benchmark.rendering

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import com.yfy.basearchitecture.benchmark.model.FrameTimingMetrics
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper.JANK_ITERATIONS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Jank Measurement Benchmark
 *
 * Jank = frame took > 16.67ms (60 FPS) or > 11.11ms (90 FPS)
 * Google considers > 2% jank as poor performance
 *
 * This is calculated from FrameTimingMetric P95/P99 values:
 * - P95 < 16.67ms = Good (60 FPS)
 * - P95 > 16.67ms = Janky
 * - P99 > 20ms = Very Janky
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class JankMetricBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Test 1: Continuous Smooth Scroll
     *
     * Measures jank during sustained scrolling
     * Simulates real user behavior: smooth continuous scrolling
     */
    @Test
    fun continuousScrollJankTest() {
        var scrollCount = 0
        var actualDuration = 0L
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = JANK_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
                device.wait(Until.hasObject(By.scrollable(true)), 3000)
                Thread.sleep(500)
            }
        ) {
            println("========================================")
            println("TEST: Continuous Smooth Scroll (30s)")
            println("========================================")

            val testDurationMs = 20000L
            val scrollIntervalMs = 100L
            val directionChangeIntervalMs = 10000L // Change direction every 10s

            val startTime = System.currentTimeMillis()
            val endTime = startTime + testDurationMs

            var direction = Direction.DOWN
            var lastDirectionChange = startTime

            println("Starting continuous scroll...")

            while (System.currentTimeMillis() < endTime) {
                // Find product grid fresh (avoid stale reference)
                val productGrid = device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                ).maxByOrNull { it.visibleBounds.height() }

                if (productGrid != null) {
                    try {
                        // Smooth scroll (0.5f = half screen)
                        productGrid.scroll(direction, 0.5f)
                        scrollCount++

                        Thread.sleep(scrollIntervalMs)
                    } catch (e: Exception) {
                        println("Scroll error at ${scrollCount}: ${e.message}")
                        // Might have hit end, change direction
                        direction = if (direction == Direction.DOWN) Direction.UP else Direction.DOWN
                        Thread.sleep(200)
                    }
                } else {
                    println("Product grid not found, waiting...")
                    Thread.sleep(500)
                }

                // Change direction every 10 seconds
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastDirectionChange >= directionChangeIntervalMs) {
                    direction = if (direction == Direction.DOWN) Direction.UP else Direction.DOWN
                    lastDirectionChange = currentTime
                    println("Direction changed to: $direction (scroll count: $scrollCount)")
                }
            }

            actualDuration = System.currentTimeMillis() - startTime
            println("\n========================================")
            println("✅ Test completed")
            println("   Duration: ${actualDuration / 1000}s")
            println("   Total scrolls: $scrollCount")
            println("   Avg scroll rate: ${scrollCount / (actualDuration / 1000.0)}/s")
            println("========================================")
        }

        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Continuous_Scroll_Jank",
                totalDurationMs = actualDuration,
                actionCount = scrollCount,
                actionType = "continuous_scroll",
                additionalInfo = mapOf(
                    "test_duration_seconds" to "30",
                    "scroll_rate_per_sec" to String.format("%.2f", scrollCount / (actualDuration / 1000.0))
                )
            )
        )
    }

    /**
     * Test 2: Rapid Direction Changes (High Stress)
     *
     * Measures jank during rapid direction changes
     * More stressful than continuous scroll
     */
    @Test
    fun rapidDirectionChangeJankTest() {
        var scrollCount = 0
        var actualDuration = 0L
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = JANK_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
                device.wait(Until.hasObject(By.scrollable(true)), 3000)
                Thread.sleep(500)
            }
        ) {
            println("========================================")
            println("TEST: Rapid Direction Changes (15s)")
            println("========================================")

            // 15 seconds of rapid direction changes
            val testDurationMs = 15000L
            val scrollsPerDirection = 3 // 3 scrolls then change direction

            val startTime = System.currentTimeMillis()
            val endTime = startTime + testDurationMs

            var direction = Direction.DOWN

            println("Starting rapid direction changes for 15 seconds...")

            while (System.currentTimeMillis() < endTime) {
                val productGrid = device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                ).maxByOrNull { it.visibleBounds.height() }

                if (productGrid != null) {
                    try {
                        // Scroll current direction
                        repeat(scrollsPerDirection) {
                            productGrid.scroll(direction, 0.6f)
                            Thread.sleep(80) // Fast scrolling
                            scrollCount++
                        }

                        // Immediately change direction (stressful for rendering)
                        direction = if (direction == Direction.DOWN) Direction.UP else Direction.DOWN

                    } catch (e: Exception) {
                        println("Scroll error: ${e.message}")
                        direction = if (direction == Direction.DOWN) Direction.UP else Direction.DOWN
                        Thread.sleep(200)
                    }
                } else {
                    Thread.sleep(500)
                }
            }

            actualDuration = System.currentTimeMillis() - startTime
            println("\n========================================")
            println("✅ Test completed")
            println("   Duration: ${actualDuration / 1000}s")
            println("   Total scrolls: $scrollCount")
            println("   Direction changes: ~${scrollCount / scrollsPerDirection}")
            println("========================================")
        }

        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Rapid_DirectionChange_Jank",
                totalDurationMs = actualDuration,
                actionCount = scrollCount,
                actionType = "rapid_direction_change",
                additionalInfo = mapOf(
                    "test_duration_seconds" to "15",
                    "scrolls_per_direction" to "3"
                )
            )
        )
    }

    /**
     * Test 3: Fling Jank Test
     *
     * Measures jank during rapid fling gestures
     * Tests animation performance
     */
    @Test
    fun flingJankTest() {
        val startTime = System.currentTimeMillis()
        var flingCount = 0
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = JANK_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
                device.wait(Until.hasObject(By.scrollable(true)), 3000)
                Thread.sleep(500)
            }
        ) {
            println("========================================")
            println("TEST: Fling Jank (20 flings)")
            println("========================================")

            val productGrid = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            if (productGrid != null) {
                // 20 rapid flings (10 down, 10 up)
                println("Flinging down 10 times...")
                repeat(10) {
                    try {
                        productGrid.fling(Direction.DOWN)
                        flingCount++
                        Thread.sleep(300) // Wait for animation
                        println("  Fling down ${it + 1}/10")
                    } catch (e: Exception) {
                        println("  Fling error: ${e.message}")
                    }
                }

                Thread.sleep(500)

                println("Flinging up 10 times...")
                repeat(10) {
                    try {
                        productGrid.fling(Direction.UP)
                        flingCount++
                        Thread.sleep(300)
                        println("  Fling up ${it + 1}/10")
                    } catch (e: Exception) {
                        println("  Fling error: ${e.message}")
                        return@repeat
                    }
                }

                println("✅ 20 flings completed")
            } else {
                println("ERROR: Product grid not found")
            }

            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Fling_Jank",
                totalDurationMs = totalDuration,
                actionCount = flingCount,
                actionType = "fling"
            )
        )
    }
}