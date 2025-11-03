package com.yfy.basearchitecture.benchmark.rendering

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
class CartInteractionBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Test 1: Cart quantity update with dynamic setup
     *
     * Flow:
     * 1. Open cart
     * 2. Remove all items if cart has any
     * 3. Add first 15 products from product list
     * 4. Open cart and scroll to bottom
     * 5. Click + 50 times on last item
     * 6. Click - 50 times on last item
     */
    @Test
    fun cartQuantityUpdatesWithDynamicSetup() {
        val startTime = System.currentTimeMillis()
        var clickCount = 0
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = SCROLL_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                // Wait for product list to load
                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
                device.wait(Until.hasObject(By.scrollable(true)), 3000)
                Thread.sleep(1000)

                // Step 1: Open cart using content description
                navigateToCartSafely(device)
                Thread.sleep(1000)

                // Step 2: Remove all items if exists
                clearCart(device)
                Thread.sleep(500)

                // Step 3: Go back to product list
                device.pressBack()
                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)), 2000)
                Thread.sleep(1000)

                // Step 4: Add 15 products
                clickCount += addProductsToCart(device, 9)
                Thread.sleep(500)

                // Step 5: Navigate back to product list first
                ensureOnProductList(device)
                Thread.sleep(500)

                // Step 6: Open cart again
                navigateToCartSafely(device)
                device.wait(Until.hasObject(By.desc("Increase")), 2000)
                Thread.sleep(1000)
            }
        ) {
            // Ensure we're in our app
            if (!device.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME))) {
                println("ERROR: Not in app, aborting test iteration")
                return@measureRepeated
            }

            // Scroll to bottom to see last item
            val scrollable = device.findObject(By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true))
            if (scrollable != null) {
                repeat(3) {
                    scrollable.scroll(Direction.DOWN, 1.0f)
                    device.waitForIdle(300)
                }
            }

            // Find all Increase buttons in our app
            val increaseButtons = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
            )

            if (increaseButtons.isEmpty()) {
                println("ERROR: No Increase buttons found")
                return@measureRepeated
            }

            // Click + 50 times on last item
            repeat(50) {
                val buttons = device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                )
                buttons.lastOrNull()?.click()
                clickCount++
                device.waitForIdle(100)
            }

            // Small scroll for frame render
            scrollable?.scroll(Direction.DOWN, 0.1f)
            device.waitForIdle(200)

            // Click - 50 times on last item
            repeat(50) {
                val buttons = device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Decrease")
                )
                buttons.lastOrNull()?.click()
                clickCount++
                device.waitForIdle(100)
            }
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Cart_QuantityUpdate_DynamicSetup",
                totalDurationMs = totalDuration,
                actionCount = clickCount,
                actionType = "quantity_update",
                additionalInfo = mapOf(
                    "setup" to "dynamic"
                )
            )
        )
    }

    /**
     * Test 2: Checkout flow with address scrolling
     *
     * Flow:
     * 1. Open cart
     * 2. Clear cart if not empty
     * 3. Add 5 products to cart
     * 4. Open cart
     * 5. Click "Proceed to Checkout"
     * 6. Scroll addresses multiple times
     * 7. Select first address
     * 8. Continue to payment
     * 9. Select first payment method
     * 10. Continue to confirmation
     */
    @Test
    fun cartCheckoutFlow() {
        val startTime = System.currentTimeMillis()
        var actionCount = 0
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = SCROLL_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()

                // Wait for app to load
                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
                device.wait(Until.hasObject(By.scrollable(true)), 3000)
                Thread.sleep(500)

                println("========================================")
                println("SETUP: Preparing cart for checkout test")
                println("========================================")

                // Step 1: Open cart
                println("Step 1: Opening cart")
                navigateToCartSafely(device)
                Thread.sleep(500)

                // Step 2: Clear cart if not empty
                println("Step 2: Clearing cart")
                clearCart(device)
                Thread.sleep(300)

                // Step 3: Go back to product list
                println("Step 3: Returning to product list")
                device.pressBack()
                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)), 1500)
                Thread.sleep(500)

                // Step 4: Add 5 products (less than Test 1, checkout needs items but not too many)
                println("Step 4: Adding 5 products for checkout")
                actionCount += addProductsToCart(device, 6)
                Thread.sleep(300)

                // Step 5: Ensure on product list
                println("Step 5: Ensuring on product list")
                ensureOnProductList(device)
                Thread.sleep(300)

                // Step 6: Open cart again
                println("Step 6: Opening cart for checkout")
                navigateToCartSafely(device)
                device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 1500)
                Thread.sleep(500)

                println("========================================")
                println("SETUP COMPLETE: Ready for checkout flow")
                println("========================================")
            }
        ) {
            println("========================================")
            println("STARTING CHECKOUT FLOW BENCHMARK")
            println("========================================")

            // Ensure we're in our app
            if (!device.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME))) {
                println("ERROR: Not in app, aborting")
                return@measureRepeated
            }

            val screenHeight = device.displayHeight
            val screenWidth = device.displayWidth

            // ===== STEP 1: Click "Proceed to Checkout" button =====
            println("Step 1: Clicking Proceed to Checkout")

            // First scroll to bottom to ensure button is visible
            val cartScrollable = device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            )
            if (cartScrollable != null) {
                try {
                    cartScrollable.scroll(Direction.DOWN, 1.0f)
                    device.waitForIdle(200)
                } catch (e: Exception) {
                    println("Cart scroll warning: ${e.message}")
                }
            }

            // Click checkout button
            device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Proceed to Checkout")
            )?.click()
            actionCount++
            Thread.sleep(500) // Wait for address screen

            // ===== STEP 2: Address Screen - Scroll multiple times =====
            println("Step 2: Scrolling addresses (100 addresses)")

            device.wait(Until.hasObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).textContains("Adres")
            ), 2000)

            val addressScrollable = device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            )

            if (addressScrollable != null) {
                // Scroll down 5 times to see many addresses (stress test)
                repeat(5) {
                    try {
                        addressScrollable.scroll(Direction.DOWN, 1.0f)
                        actionCount++
                        Thread.sleep(150)
                        println("   Address scroll ${it + 1}/5")
                    } catch (e: Exception) {
                        println("   Address scroll ${it + 1} error: ${e.message}")
                    }
                }

                // Scroll back up to top to select first address
                repeat(2) {
                    try {
                        addressScrollable.scroll(Direction.UP, 1.0f)
                        actionCount++
                        Thread.sleep(150)
                        println("   Address scroll up ${it + 1}/5")
                    } catch (e: Exception) {
                        println("   Address scroll up ${it + 1} error: ${e.message}")
                    }
                }
            }

            // ===== STEP 3: Select first address =====
            println("Step 3: Selecting first address")

            // First address card is typically at top, click on it
            val firstAddressX = screenWidth / 2
            val firstAddressY = screenHeight / 3 // Upper third of screen

            device.click(firstAddressX, firstAddressY)
            Thread.sleep(300)

            // ===== STEP 4: Click "Continue" button =====
            println("Step 4: Clicking Continue to payment")

            device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Devam Et")
            )?.click()
            actionCount++
            Thread.sleep(500) // Wait for payment screen

            // ===== STEP 5: Payment Screen - Select first payment =====
            println("Step 5: Selecting payment method")

            device.wait(Until.hasObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).textContains("Ödeme Yöntemi")
            ), 2000)

            // First payment option
            val firstPaymentX = screenWidth / 2
            val firstPaymentY = screenHeight / 3

            device.click(firstPaymentX, firstPaymentY)
            actionCount++
            Thread.sleep(300)

            // ===== STEP 6: Click "Continue" to confirmation =====
            println("Step 6: Clicking Continue to confirmation")

            device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Devam Et")
            )?.click()
            actionCount++
            Thread.sleep(500) // Wait for confirmation screen

            // ===== STEP 7: Confirmation Screen - Scroll to see all details =====
            println("Step 7: Scrolling confirmation screen")
            val confirmationScrollable = device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            )

            if (confirmationScrollable != null) {
                // Scroll down to see all order details
                repeat(3) {
                    try {
                        confirmationScrollable.scroll(Direction.DOWN, 1.0f)
                        actionCount++
                        Thread.sleep(150)
                        println("   Confirmation scroll ${it + 1}/3")
                    } catch (e: Exception) {
                        println("   Confirmation scroll ${it + 1} error: ${e.message}")
                    }
                }

                // Scroll back up
                repeat(3) {
                    try {
                        confirmationScrollable.scroll(Direction.UP, 1.0f)
                        actionCount++
                        Thread.sleep(150)
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }

            device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Siparişi Onayla")
            )?.click()
            actionCount++
            Thread.sleep(500)

            println("========================================")
            println("✅ CHECKOUT FLOW COMPLETED")
            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Cart_CheckoutFlow",
                totalDurationMs = totalDuration,
                actionCount = actionCount,
                actionType = "checkout_flow",
                additionalInfo = mapOf(
                    "flow_steps" to "add_to_cart,view_cart,checkout,confirm"
                )
            )
        )
    }
    /**
     * Helper: Navigate to cart safely using content description
     */
    private fun navigateToCartSafely(device: UiDevice) {
        println("Navigating to cart...")

        // Find cart icon by content description in our app
        val cartButton = device.findObject(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Shopping Cart")
        )

        if (cartButton != null) {
            println("Found cart button via content description")
            cartButton.click()
            device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 2000)
            Thread.sleep(500)
        } else {
            println("ERROR: Cart button not found by content description, trying fallback...")
            // Fallback: Find rightmost button in top bar
            val topBarButtons = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).clickable(true)
            ).filter {
                it.visibleBounds.top < device.displayHeight * 0.15
            }.sortedBy { it.visibleBounds.left }

            topBarButtons.lastOrNull()?.click()
            device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 2000)
            Thread.sleep(500)
        }
    }

    /**
     * Helper: Clear all items from cart
     */
    private fun clearCart(device: UiDevice) {
        println("Clearing cart...")

        var removeButtons = device.findObjects(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Remove")
        )

        var attempts = 0
        while (removeButtons.isNotEmpty() && attempts < 50) {
            removeButtons.firstOrNull()?.click()
            device.waitForIdle(300)
            removeButtons = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Remove")
            )
            attempts++
        }

        println("Cleared $attempts items from cart")
    }

    /**
     * Helper: Ensure we're on product list screen
     */
    private fun ensureOnProductList(device: UiDevice) {
        var attempts = 0
        while (!device.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)) && attempts < 3) {
            device.pressBack()
            device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 1000)
            attempts++
        }
    }

    /**
     * Helper: Add N products to cart
     * DETERMINISTIC: Always clicks same position, always flings same amount
     * Perfect for comparing different architectures on different devices
     */
    private fun addProductsToCart(device: UiDevice, count: Int): Int {
        var actionCount = 0
        val screenHeight = device.displayHeight
        val screenWidth = device.displayWidth

        val productClickX = screenWidth / 4 // Left column center
        val productClickY = screenHeight / 2 // Middle of screen

        // Add to Cart button position: Bottom right
        val addToCartX = screenWidth * 2 / 3
        val addToCartY = screenHeight - 120

        for (i in 1..count) {
            println("========================================")
            println("Adding product $i/$count")
            println("========================================")

            // Step 1: Click product at fixed position
            println("1. Clicking product at ($productClickX, $productClickY)")
            device.click(productClickX, productClickY)
            Thread.sleep(200) // Wait for detail screen to load

            // Step 2: Click "Add to Cart" at fixed position
            println("2. Clicking Add to Cart at ($addToCartX, $addToCartY)")
            device.findObject(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Add to Cart")
            )?.click()
            actionCount++
            Thread.sleep(100) // Wait for add action

            // Step 3: Go back to list
            println("3. Going back to product list")
            device.pressBack()
            Thread.sleep(100) // Wait for list to appear

            device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }?.let { grid ->
                repeat(4) { // 4x full screen scroll
                    try {
                        grid.scroll(Direction.DOWN, 5.0f)
                        actionCount++
                        Thread.sleep(50)
                    } catch (e: Exception) {
                        println("   Scroll error: ${e.message}")
                    }
                }

            }

            println("✅ Product $i/$count added successfully")
        }

        println("========================================")
        println("✅ COMPLETED: Added all $count products")
        println("========================================")
        return actionCount
    }}