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
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper.SCROLL_ITERATIONS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ProductListScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Test 1: Product Grid Scrolling with Pagination
     * Uses fling for long scrolls through 1000 items
     */
    @Test
    fun productListScrollAndPagination() {
        val startTime = System.currentTimeMillis()
        var scrollCount = 0
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = SCROLL_ITERATIONS,
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
            println("TEST: Product Grid Scrolling")
            println("========================================")

            // Find product grid (tallest scrollable)
            val productGrid = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            if (productGrid != null) {
                println("Found product grid: height=${productGrid.visibleBounds.height()}")

                // Scroll down 5 times with fling (long distance through mock data)
                println("Scrolling DOWN...")
                repeat(5) {
                    try {
                        productGrid.fling(Direction.DOWN)
                        scrollCount++
                        println("   Fling down ${it + 1}/10")
                    } catch (e: Exception) {
                        println("   Fling down ${it + 1} error: ${e.message}")
                    }
                }

                Thread.sleep(300) // Pause between directions

                // Scroll back up 5 times
                println("Scrolling UP...")
                repeat(5) {
                    try {
                        productGrid.fling(Direction.UP)
                        scrollCount++
                        println("   Fling up ${it + 1}/10")
                    } catch (e: Exception) {
                        println("   Fling up ${it + 1} error: ${e.message}")
                    }
                }

                println("✅ Scrolling test completed")
            } else {
                println("ERROR: Product grid not found!")
            }

            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime

        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "ProductList_Scroll",
                totalDurationMs = totalDuration,
                actionCount = scrollCount,
                actionType = "fling",
                additionalInfo = mapOf(
                    "flings_down" to "10",
                    "flings_up" to "10",
                    "total_flings" to scrollCount.toString()
                )
            )
        )
    }

    /**
     * Test 2: Category Filtering (Click different categories)
     * Finds category chips dynamically, scrolls category row horizontally
     */
    @Test
    fun productListCategoryFiltering() {
        val startTime = System.currentTimeMillis()
        var categoryClickCount = 0
        var productScrollCount = 0
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = SCROLL_ITERATIONS,
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
            println("TEST: Category Filtering")
            println("========================================")

            // Find all scrollables
            val allScrollables = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            )

            // Category LazyRow is the SHORTEST scrollable
            val categoryRow = allScrollables.minByOrNull {
                it.visibleBounds.height()
            }

            // Find product grid (tallest scrollable)
            val productGrid = allScrollables.maxByOrNull {
                it.visibleBounds.height()
            }

            if (categoryRow != null && productGrid != null) {
                println("Found category row: height=${categoryRow.visibleBounds.height()}")
                println("Found product grid: height=${productGrid.visibleBounds.height()}")

                // Click categories while scrolling category row horizontally
                // This ensures we test different categories across the horizontal list
                val totalCategoriesToTest = 6 // Test 6 different categories

                repeat(totalCategoriesToTest) { iteration ->
                    println("Category selection ${iteration + 1}/$totalCategoriesToTest")

                    // Find currently visible category chips
                    val visibleChips = try {
                        categoryRow.children
                            .filter { it.isClickable }
                            .take(3) // Only 3 visible at a time
                    } catch (e: Exception) {
                        println("Error finding chips: ${e.message}")
                        emptyList()
                    }

                    if (visibleChips.isEmpty()) {
                        println("No visible chips found, stopping")
                        return@measureRepeated
                    }

                    println("Found ${visibleChips.size} visible chips")

                    // Click the FIRST visible chip (after each scroll, this will be a new category)
                    val chipToClick = visibleChips.firstOrNull()

                    if (chipToClick != null) {
                        try {
                            println("Clicking first visible chip")
                            chipToClick.click()
                            categoryClickCount++
                            Thread.sleep(200) // Wait for filter to apply
                        } catch (e: Exception) {
                            println("Click error: ${e.message}")
                        }

                        // Scroll product grid to see filtered results (DETERMINISTIC)
                        println("Scrolling product grid...")
                        try {
                            // Fling down 3 times
                            repeat(3) {
                                productGrid.fling(Direction.DOWN)
                                productScrollCount++
                                Thread.sleep(120)
                            }

                            // Fling back up to top
                            repeat(3) {
                                productGrid.fling(Direction.UP)
                                productScrollCount++
                                Thread.sleep(120)
                            }

                            println("✅ Product grid scrolled")
                        } catch (e: Exception) {
                            println("Product scroll error: ${e.message}")
                        }

                        // Scroll category row LEFT to reveal next categories
                        // (Only if not the last iteration)
                        if (iteration < totalCategoriesToTest - 1) {
                            println("Scrolling category row LEFT to reveal new categories")
                            try {
                                categoryRow.scroll(Direction.RIGHT, 0.7f)
                                Thread.sleep(50)
                                println("✅ Category row scrolled left")
                            } catch (e: Exception) {
                                println("Category scroll error: ${e.message}")
                                // If we can't scroll anymore, we've reached the end
                                println("Reached end of category list at iteration ${iteration + 1}")
                                return@measureRepeated
                            }
                        }
                    } else {
                        println("No chip to click found")
                    }
                }

                println("✅ Category filtering test completed")
            } else {
                println("ERROR: Category row or product grid not found!")
                if (categoryRow == null) println("   Missing: Category row")
                if (productGrid == null) println("   Missing: Product grid")
            }

            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "ProductList_CategoryFilter",
                totalDurationMs = totalDuration,
                actionCount = categoryClickCount + productScrollCount,
                actionType = "filter_and_scroll",
                additionalInfo = mapOf(
                    "category_clicks" to categoryClickCount.toString(),
                    "product_scrolls" to productScrollCount.toString()
                )
            )
        )
    }    /**
     * Test 3: Rapid Scrolling (Stress Test)
     * DETERMINISTIC: Maximum stress with rapid flings
     */
    @Test
    fun productListRapidScrolling() {
        val startTime = System.currentTimeMillis()
        var scrollCount = 0
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = SCROLL_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()
                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
                device.wait(Until.hasObject(By.scrollable(true)), 3000)
                Thread.sleep(300)
            }
        ) {
            println("========================================")
            println("TEST: Rapid Scrolling (Stress)")
            println("========================================")

            // Find product grid (tallest scrollable)
            val productGrid = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            if (productGrid != null) {
                println("Starting rapid scrolling: height=${productGrid.visibleBounds.height()}")

                // Rapid fling down 10 times (maximum stress)
                println("RAPID fling DOWN...")
                repeat(10) {
                    try {
                        productGrid.fling(Direction.DOWN)
                        scrollCount++
                        Thread.sleep(100) // Shorter delay for "rapid"
                        println("Fling down ${it + 1}/15")
                    } catch (e: Exception) {
                        println("Fling error: ${e.message}")
                    }
                }

                Thread.sleep(200) // Brief pause

                // Rapid fling up 10 times
                println("RAPID fling UP...")
                repeat(10) {
                    try {
                        productGrid.fling(Direction.UP)
                        scrollCount++
                        Thread.sleep(100)
                        println("Fling up ${it + 1}/15")
                    } catch (e: Exception) {
                        println("Fling error: ${e.message}")
                    }
                }

                println("✅ Rapid scrolling stress test completed")
            } else {
                println("ERROR: Product grid not found!")
            }

            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "ProductList_RapidScroll",
                totalDurationMs = totalDuration,
                actionCount = scrollCount,
                actionType = "rapid_fling"
            )
        )
    }
}