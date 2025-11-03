package com.yfy.basearchitecture.benchmark.memory

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.Debug
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.yfy.basearchitecture.benchmark.model.MemoryMetrics
import com.yfy.basearchitecture.benchmark.model.MemorySnapshot
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper.MEMORY_ITERATIONS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MemoryUsageBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val memorySnapshots = mutableListOf<MemorySnapshot>()

    /**
     * Complete flow memory usage with GC monitoring
     */
    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun memoryUsageCompleteFlow() {
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(TraceSectionMetric("MemoryMonitoring")),
            compilationMode = CompilationMode.DEFAULT,
            iterations = MEMORY_ITERATIONS,
            startupMode = StartupMode.COLD,
            setupBlock = {
                pressHome()
                Thread.sleep(2000)
                memorySnapshots.clear()
                Runtime.getRuntime().gc()
                Thread.sleep(500)
            }
        ) {
            println("========================================")
            println("MEMORY USAGE & GC MONITORING - Complete Flow")
            println("========================================")

            // === PHASE 1: App Launch ===
            startActivityAndWait()
            device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 3000)
            device.wait(Until.hasObject(By.scrollable(true)), 3000)
            Thread.sleep(1000)
            captureMemory("01_AppLaunch")

            // === PHASE 2: Product List (Initial) ===
            println("\n--- Phase 2: Product List Initial ---")
            captureMemory("02_ProductList_Initial")

            // === PHASE 3: Product List (Heavy Scrolling - Memory Pressure) ===
            println("\n--- Phase 3: Heavy Scrolling (Memory Pressure) ---")
            val productGrid = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            if (productGrid != null) {
                // Heavy scrolling to create memory pressure
                repeat(10) {
                    try {
                        productGrid.fling(Direction.DOWN)
                        Thread.sleep(200)
                    } catch (e: Exception) {
                        println("Fling error: ${e.message}")
                    }
                }
                captureMemory("03_ProductList_AfterHeavyScroll")

                // Scroll back
                repeat(10) {
                    try {
                        productGrid.fling(Direction.UP)
                        Thread.sleep(200)
                    } catch (e: Exception) {
                        return@repeat
                    }
                }
            }

            // === PHASE 4: Product Detail ===
            println("\n--- Phase 4: Product Detail ---")
            val screenWidth = device.displayWidth
            val screenHeight = device.displayHeight

            device.click(screenWidth / 4, screenHeight / 2)
            device.wait(Until.hasObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Back")
            ), 2000)
            Thread.sleep(500)
            captureMemory("04_ProductDetail")

            device.pressBack()
            Thread.sleep(500)

            // === PHASE 5: Cart Screen ===
            println("\n--- Phase 5: Cart Screen ---")
            val cartButton = device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Shopping Cart")
            )

            if (cartButton != null) {
                cartButton.click()
                Thread.sleep(500)

                // Check if empty, add items if needed
                val hasItems = device.hasObject(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                )

                if (!hasItems) {
                    println("Cart empty, adding items...")
                    device.pressBack()
                    Thread.sleep(300)
                    addProductsQuickly(device, 3)
                    cartButton.click()
                    Thread.sleep(500)
                }

                captureMemory("05_Cart_Initial")

                // === PHASE 6: Cart Updates (Memory Pressure) ===
                println("\n--- Phase 6: Rapid Cart Updates ---")
                repeat(50) { // 50 rapid updates to stress memory
                    val increaseButtons = device.findObjects(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                    )
                    increaseButtons.firstOrNull()?.click()
                    Thread.sleep(50) // Very fast updates
                }
                captureMemory("06_Cart_AfterRapidUpdates")

                device.pressBack()
                Thread.sleep(500)
            }

            // === PHASE 7: Chat List ===
            println("\n--- Phase 7: Chat List ---")
            val messageBoxButton = device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Message Box")
            )

            if (messageBoxButton != null) {
                messageBoxButton.click()
                Thread.sleep(800)
                captureMemory("07_ChatList")

                // === PHASE 8: Chat Detail ===
                println("\n--- Phase 8: Chat Detail ---")
                val chatList = device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                ).maxByOrNull { it.visibleBounds.height() }

                val firstChat = chatList?.children
                    ?.filter { it.isClickable }
                    ?.filter {
                        try {
                            it.visibleBounds.height() > 60
                        } catch (e: Exception) {
                            false
                        }
                    }
                    ?.firstOrNull()

                if (firstChat != null) {
                    firstChat.click()
                    Thread.sleep(500)

                    device.wait(
                        Until.hasObject(
                            By.pkg(BenchmarkHelper.PACKAGE_NAME).clazz("android.widget.EditText")
                        ),
                        2000
                    )

                    captureMemory("08_ChatDetail_Initial")

                    // === PHASE 9: Message Stream (Memory Accumulation) ===
                    println("\n--- Phase 9: Message Stream (15 seconds) ---")
                    Thread.sleep(15000) // Wait for ~30 messages (500ms interval)
                    captureMemory("09_ChatDetail_AfterStream")

                    device.pressBack()
                    Thread.sleep(500)
                }
            }

            // === PHASE 10: Peak Memory After All Operations ===
            println("\n--- Phase 10: Peak Memory ---")
            device.pressBack() // Back to product list
            Thread.sleep(1000)
            captureMemory("10_Peak_AfterAllOperations")

            println("\n========================================")
            println("Memory monitoring completed")
            println("Total snapshots: ${memorySnapshots.size}")
            println("========================================")
        }

        BenchmarkHelper.exportMemoryResults(
            MemoryMetrics(
                testName = "CompleteFlow",
                snapshots = memorySnapshots
            )
        )
    }

    /**
     * Capture comprehensive memory metrics including GC info
     */
    @SuppressLint("DefaultLocale")
    private fun captureMemory(label: String) {
        try {
            Runtime.getRuntime().gc()
            Thread.sleep(200)

            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val activityManager = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as ActivityManager

            val pid = android.os.Process.myPid()
            val processMemoryInfo = activityManager.getProcessMemoryInfo(intArrayOf(pid))

            if (processMemoryInfo.isNotEmpty()) {
                val memInfo = processMemoryInfo[0]
                val debugMemInfo = Debug.MemoryInfo()
                Debug.getMemoryInfo(debugMemInfo)

                // Runtime memory
                val runtime = Runtime.getRuntime()
                val totalMemory = runtime.totalMemory()
                val freeMemory = runtime.freeMemory()
                val usedMemory = totalMemory - freeMemory
                val maxMemory = runtime.maxMemory()
                val memoryPressure = (usedMemory.toDouble() / maxMemory.toDouble()) * 100

                val snapshot = MemorySnapshot(
                    label = label,
                    timestamp = System.currentTimeMillis(),

                    // Process memory (KB -> MB)
                    totalPss = memInfo.totalPss / 1024.0,
                    totalPrivateDirty = memInfo.totalPrivateDirty / 1024.0,
                    totalSharedDirty = memInfo.totalSharedDirty / 1024.0,
                    nativeHeap = debugMemInfo.nativePrivateDirty / 1024.0,
                    dalvikHeap = debugMemInfo.dalvikPrivateDirty / 1024.0,
                    otherPss = memInfo.otherPss / 1024.0,

                    // Runtime memory (Bytes -> MB)
                    usedMemoryMB = usedMemory / (1024.0 * 1024.0),
                    totalMemoryMB = totalMemory / (1024.0 * 1024.0),
                    maxMemoryMB = maxMemory / (1024.0 * 1024.0),
                    freeMemoryMB = freeMemory / (1024.0 * 1024.0),

                    // Memory pressure
                    memoryPressurePercent = memoryPressure
                )

                memorySnapshots.add(snapshot)

                println("\n[$label]")
                println("  Total PSS: ${String.format("%.2f", snapshot.totalPss)} MB")
                println("  Used/Max: ${String.format("%.2f", snapshot.usedMemoryMB)}/${String.format("%.2f", snapshot.maxMemoryMB)} MB")
                println("  Pressure: ${String.format("%.1f", snapshot.memoryPressurePercent)}%")
            }
        } catch (e: Exception) {
            println("Error capturing memory: ${e.message}")
        }
    }
    /**
     * Helper: Quick product addition
     */
    private fun addProductsQuickly(device: UiDevice, count: Int) {
        val screenHeight = device.displayHeight
        val screenWidth = device.displayWidth

        val productX = screenWidth / 4
        val productY = screenHeight / 2

        repeat(count) {
            device.click(productX, productY)
            Thread.sleep(300)
            device.findObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Add to Cart")
            )?.click()
            Thread.sleep(200)
            device.pressBack()
            Thread.sleep(200)

            val grid = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            try {
                grid?.scroll(Direction.DOWN, 0.3f)
                Thread.sleep(200)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}