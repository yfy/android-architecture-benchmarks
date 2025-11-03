package com.yfy.basearchitecture.benchmark.interaction

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.yfy.basearchitecture.benchmark.model.FrameTimingMetrics
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper.SCROLL_ITERATIONS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class StateUpdateBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Test 1: Cart Quantity Update Performance
     *
     * Measures state update performance via rapid + button clicks
     * Setup: Ensures cart has items before testing
     */
    @Test
    fun cartQuantityUpdatePerformance() {
        val startTime = System.currentTimeMillis()
        var totalClickCount = 0
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

                println("========================================")
                println("SETUP: Preparing cart for state update test")
                println("========================================")

                // Navigate to Cart
                navigateToCart(device)
                Thread.sleep(500)

                // Check if cart is empty
                val hasIncreaseButtons = device.hasObject(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                )

                if (!hasIncreaseButtons) {
                    println("Cart is empty, adding products...")

                    // Go back to product list
                    device.pressBack()
                    Thread.sleep(500)

                    // Add 3 products using deterministic approach
                    addProductsForTest(device, 3)

                    // Return to cart
                    navigateToCart(device)
                    Thread.sleep(500)

                    println("✅ Products added to cart")
                } else {
                    println("✅ Cart already has items")
                }

                // Verify cart has items now
                val hasItems = device.wait(
                    Until.hasObject(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                    ),
                    2000
                )

                if (!hasItems) {
                    throw Exception("Cart still empty after setup")
                }

                println("========================================")
                println("SETUP COMPLETE")
                println("========================================")
            }
        ) {
            println("========================================")
            println("TEST: Rapid Quantity Updates")
            println("========================================")

            // Rapid clicks on + button to measure state update responsiveness
            repeat(50) { clickCount ->
                // Find Increase buttons fresh each time (avoid stale)
                val increaseButtons = device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                )

                if (increaseButtons.isNotEmpty()) {
                    increaseButtons.firstOrNull()?.click()
                    totalClickCount++
                    device.waitForIdle(100) // Minimal wait

                    if (clickCount % 10 == 0) {
                        println("  Clicked + button ${clickCount}/50 times")
                    }
                } else {
                    println("  No Increase buttons found at click $clickCount")
                    return@repeat
                }
            }

            println("✅ Completed 50 rapid clicks")
            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Cart_StateUpdate",
                totalDurationMs = totalDuration,
                actionCount = totalClickCount,
                actionType = "button_click",
                additionalInfo = mapOf(
                    "target" to "increase_quantity"
                )
            )
        )
    }

    /**
     * Test 2: Category Filter Performance
     *
     * Measures state update performance via rapid category switches
     */
    @Test
    fun categoryFilterPerformance() {
        val startTime = System.currentTimeMillis()
        var chipClickCount = 0
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
            println("TEST: Rapid Category Filtering")
            println("========================================")

            val screenWidth = device.displayWidth
            val categoryY = 350 // Category row Y position

            // Find all scrollables
            val allScrollables = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            )

            // Category row is shortest scrollable
            val categoryRow = allScrollables.minByOrNull { it.visibleBounds.height() }

            if (categoryRow != null) {
                println("Found category row")

                // Get visible category chips
                val visibleChips = try {
                    categoryRow.children
                        .filter { it.isClickable }
                        .take(5) // First 5 visible
                } catch (e: Exception) {
                    println("Could not get chips: ${e.message}")
                    emptyList()
                }

                if (visibleChips.isEmpty()) {
                    println("No category chips found, using coordinates")

                    val categoryPositions = listOf(
                        screenWidth / 6,
                        screenWidth / 6 * 2,
                        screenWidth / 6 * 3,
                        screenWidth / 6 * 4
                    )

                    // Click categories 3 times each (rapid switching)
                    repeat(3) { round ->
                        println("  Round ${round + 1}/3")
                        categoryPositions.forEach { xPosition ->
                            device.click(xPosition, categoryY)
                            chipClickCount++
                            device.waitForIdle(150)
                        }
                    }
                } else {
                    println("Found ${visibleChips.size} category chips")

                    // Click each chip 3 times (rapid switching)
                    repeat(3) { round ->
                        println("  Round ${round + 1}/3")
                        visibleChips.forEachIndexed { index, chip ->
                            try {
                                chip.click()
                                chipClickCount++
                                device.waitForIdle(150)
                            } catch (e: Exception) {
                                println("    Click error on chip $index: ${e.message}")
                            }
                        }
                    }
                }

                println("✅ Completed rapid category switching")
            } else {
                println("ERROR: Category row not found")
            }

            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Category_StateUpdate",
                totalDurationMs = totalDuration,
                actionCount = chipClickCount,
                actionType = "filter_click",
                additionalInfo = mapOf(
                    "rounds" to "3"
                )
            )
        )
    }

    /**
     * Helper: Navigate to Cart from Product List
     */
    private fun navigateToCart(device: UiDevice) {
        println("Navigating to cart...")

        // Find Shopping Cart button by descriptor
        val cartButton = device.findObject(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Shopping Cart")
        )

        if (cartButton != null) {
            cartButton.click()
            Thread.sleep(500)
            println("✅ Navigated to cart via descriptor")
        } else {
            val screenWidth = device.displayWidth
            device.click(screenWidth - 80, 100)
            Thread.sleep(500)
            println("⚠️ Navigated to cart via coordinates")
        }
    }

    /**
     * Helper: Add N products to cart quickly (for test setup)
     */
    private fun addProductsForTest(device: UiDevice, count: Int) {
        println("Adding $count products for test setup...")

        val screenHeight = device.displayHeight
        val screenWidth = device.displayWidth

        val productClickX = screenWidth / 4
        val productClickY = screenHeight / 2

        // Add to Cart button position
        val addToCartX = screenWidth * 2 / 3
        val addToCartY = screenHeight - 120

        repeat(count) { index ->
            println("  Adding product ${index + 1}/$count")

            // Click product
            device.click(productClickX, productClickY)
            Thread.sleep(300)

            // Click Add to Cart
            device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Add to Cart")
            )?.click()
            Thread.sleep(200)

            // Go back
            device.pressBack()
            Thread.sleep(200)

            // Small scroll to reveal next product
            val grid = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            try {
                grid?.scroll(Direction.DOWN, 0.3f)
                Thread.sleep(200)
            } catch (e: Exception) {
                // Ignore scroll errors
            }
        }

        println("✅ Added $count products")
    }
}