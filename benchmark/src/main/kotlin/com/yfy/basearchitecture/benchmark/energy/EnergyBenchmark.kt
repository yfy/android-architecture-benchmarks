package com.yfy.basearchitecture.benchmark.energy

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Energy benchmark tests for measuring power consumption across different architecture patterns.
 *
 * **Important Requirements:**
 * - Device must be connected to Android Studio via WiFi ADB for accurate energy measurements.
 * - Real device usage is strongly recommended.
 * - Emulator does not accurately reflect real energy consumption or usage patterns.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class EnergyBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    companion object {
        private const val TAG = "EnergyBenchmark"
        private const val ITERATIONS = 5
        private const val SHORT_ITERATIONS = 5
        private const val STABILIZATION_SECONDS = 10
        private const val MIN_CHARGE_CHANGE_MAH = 1.0
        private const val SHORT_TEST_DURATION = 30_000
        private const val LONG_TEST_DURATION = 60_000

        private val energyResults = mutableListOf<EnergyHelper.EnergyResult>()

        @AfterClass
        @JvmStatic
        fun saveResults() {
            if (energyResults.isNotEmpty()) {
                EnergyHelper.saveEnergyResults(energyResults)
                printResultsSummary()
            }
        }

        @SuppressLint("DefaultLocale")
        private fun printResultsSummary() {
            println("\n${"═".repeat(50)}")
            println("ENERGY CONSUMPTION SUMMARY")
            println("═".repeat(50))
            energyResults.forEach { result ->
                println("${result.scenarioName}:")
                println("  Energy: ${String.format("%.2f", result.energyConsumedMWh)} mWh")
                println("  Power: ${String.format("%.2f", result.averagePowerMW)} mW")
                println("  Duration: ${String.format("%.1f", result.durationSeconds)}s")
            }
            println("═".repeat(50))
        }
    }

    @Before
    fun checkDeviceReadiness() {
        checkEnergyMeasurementCapability()
        checkDeviceUnplugged()
        logDeviceState()
        validateTestConditions()
    }

    private fun checkEnergyMeasurementCapability() {
        val snapshot = EnergyHelper.captureEnergySnapshot()
        val isEnergySupported = EnergyHelper.isEnergyCounterSupported()

        Log.i(TAG, """
            ═══════════════════════════════════════════════
            DEVICE CAPABILITIES
            ═══════════════════════════════════════════════
            Device: ${android.os.Build.MODEL}
            Android: ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})
            
            Energy Counter: ${if (isEnergySupported) "✅ SUPPORTED" else "❌ NOT SUPPORTED"}
            Charge Counter: ${if (snapshot.chargeUAh > 0) "✅ SUPPORTED" else "❌ NOT SUPPORTED"}
            Battery Voltage: ${if (snapshot.voltageUV > 0) "✅ AVAILABLE" else "❌ NOT AVAILABLE"}
            
            Method: ${if (isEnergySupported) "Hardware Energy Counter" else "Charge × Voltage"}
            Status: ${if (snapshot.chargeUAh > 0) "✅ READY" else "❌ NOT SUPPORTED"}
            ═══════════════════════════════════════════════
        """.trimIndent())

        require(snapshot.chargeUAh > 0) {
            "CHARGE_COUNTER not supported on this device"
        }

        require(isEnergySupported || snapshot.voltageUV > 0) {
            "Neither ENERGY_COUNTER nor VOLTAGE available"
        }
    }

    private fun checkDeviceUnplugged() {
        if (!EnergyHelper.isUnplugged()) {
            val message = """
                ═══════════════════════════════════════════════
                ⚠️  WARNING: DEVICE IS PLUGGED IN
                ═══════════════════════════════════════════════
                Energy measurements will be INACCURATE.
                Please UNPLUG the device and run tests again.
                ═══════════════════════════════════════════════
            """.trimIndent()
            Log.w(TAG, message)
            println(message)
        }
    }

    private fun logDeviceState() {
        val state = EnergyHelper.captureDeviceState()
        Log.i(TAG, """
            Device State:
              Battery: ${state.batteryLevel}%
              Temperature: ${state.batteryTemperature}°C
              Brightness: ${state.screenBrightness}
              Airplane Mode: ${state.isAirplaneMode}
        """.trimIndent())
    }

    private fun validateTestConditions() {
        val state = EnergyHelper.captureDeviceState()

        if (state.batteryLevel !in 40..60) {
            Log.w(TAG, "⚠️ Battery level ${state.batteryLevel}% (recommended: 40-60%)")
        }

        if (state.batteryTemperature > 35.0f) {
            Log.w(TAG, "⚠️ Battery temperature ${state.batteryTemperature}°C (recommended: <35°C)")
        }

        if (!state.isAirplaneMode) {
            Log.w(TAG, "⚠️ Airplane mode disabled (recommended: enabled)")
        }
    }

    @Test
    fun productBrowsingEnergy() {
        val scenarioResults = mutableListOf<EnergyHelper.IterationResult>()

        repeat(ITERATIONS) { iteration ->
            println("=".repeat(40))
            println("Product Browsing - Iteration ${iteration + 1}/$ITERATIONS")
            println("=".repeat(40))

            var energyBefore: EnergyHelper.EnergyMeasurement? = null
            var energyAfter: EnergyHelper.EnergyMeasurement? = null
            var scrollCount = 0
            var filterCount = 0

            benchmarkRule.measureRepeated(
                packageName = BenchmarkHelper.PACKAGE_NAME,
                metrics = listOf(FrameTimingMetric()),
                compilationMode = CompilationMode.DEFAULT,
                iterations = 1,
                startupMode = StartupMode.WARM,
                setupBlock = {
                    pressHome()
                    startActivityAndWait()
                    device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 5000)
                    device.wait(Until.hasObject(By.scrollable(true)), 3000)
                    Thread.sleep(1000)

                    EnergyHelper.waitForStabilization(STABILIZATION_SECONDS)
                    energyBefore = EnergyHelper.captureEnergySnapshot()
                }
            ) {
                val endTime = System.currentTimeMillis() + LONG_TEST_DURATION
                var currentDirection = Direction.DOWN

                while (System.currentTimeMillis() < endTime) {
                    val allScrollables = device.findObjects(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                    )
                    val productGrid = allScrollables.maxByOrNull { it.visibleBounds.height() }

                    productGrid?.let {
                        try {
                            it.fling(currentDirection)
                            scrollCount++
                            Thread.sleep(300)

                            if (scrollCount % 10 == 0) {
                                val categoryRow = allScrollables.minByOrNull {
                                    it.visibleBounds.height()
                                }
                                categoryRow?.let { row ->
                                    row.children.filter { it.isClickable }
                                        .take(3)
                                        .randomOrNull()
                                        ?.click()
                                    filterCount++
                                    Thread.sleep(200)
                                }
                            }

                            if (scrollCount % 15 == 0) {
                                currentDirection = if (currentDirection == Direction.DOWN)
                                    Direction.UP else Direction.DOWN
                            }
                        } catch (e: Exception) {
                            currentDirection = if (currentDirection == Direction.DOWN)
                                Direction.UP else Direction.DOWN
                            Thread.sleep(200)
                        }
                    } ?: Thread.sleep(500)
                }

                energyAfter = EnergyHelper.captureEnergySnapshot()
            }

            processIterationResult(
                energyBefore, energyAfter,
                "Product_Browsing", iteration + 1,
                scenarioResults, scrollCount + filterCount
            )

            if (iteration < ITERATIONS - 1) Thread.sleep(2000)
        }

        finalizeScenarioResults("Product_Browsing", scenarioResults)
    }

    @Test
    fun shoppingCartEnergy() {
        val scenarioResults = mutableListOf<EnergyHelper.IterationResult>()

        repeat(SHORT_ITERATIONS) { iteration ->
            println("=".repeat(40))
            println("Shopping Cart - Iteration ${iteration + 1}/$SHORT_ITERATIONS")
            println("=".repeat(40))

            var energyBefore: EnergyHelper.EnergyMeasurement? = null
            var energyAfter: EnergyHelper.EnergyMeasurement? = null
            var clickCount = 0

            benchmarkRule.measureRepeated(
                packageName = BenchmarkHelper.PACKAGE_NAME,
                metrics = listOf(FrameTimingMetric()),
                compilationMode = CompilationMode.DEFAULT,
                iterations = 1,
                startupMode = StartupMode.WARM,
                setupBlock = {
                    pressHome()
                    startActivityAndWait()
                    device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 5000)
                    device.wait(Until.hasObject(By.scrollable(true)), 3000)
                    Thread.sleep(500)

                    navigateToCartSafely(device)
                    Thread.sleep(500)
                    clearCart(device)
                    Thread.sleep(500)
                    device.pressBack()
                    device.wait(Until.hasObject(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                    ), 2000)
                    Thread.sleep(500)

                    addProductsToCart(device, 5)
                    Thread.sleep(500)

                    ensureOnProductList(device)
                    Thread.sleep(500)

                    navigateToCartSafely(device)
                    device.wait(Until.hasObject(By.desc("Increase")), 2000)
                    Thread.sleep(500)

                    EnergyHelper.waitForStabilization(STABILIZATION_SECONDS)
                    energyBefore = EnergyHelper.captureEnergySnapshot()
                }
            ) {
                val endTime = System.currentTimeMillis() + LONG_TEST_DURATION

                while (System.currentTimeMillis() < endTime) {
                    val increaseButtons = device.findObjects(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Increase")
                    )

                    if (increaseButtons.isNotEmpty()) {
                        try {
                            increaseButtons.first().click()
                            clickCount++
                            device.waitForIdle(50)

                            if (clickCount % 20 == 0) {
                                device.findObject(
                                    By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                                )?.scroll(Direction.DOWN, 0.5f)
                                Thread.sleep(100)
                            }
                        } catch (e: Exception) {
                            Thread.sleep(100)
                        }
                    } else {
                        break
                    }
                }

                energyAfter = EnergyHelper.captureEnergySnapshot()
            }

            processIterationResult(
                energyBefore, energyAfter,
                "Shopping_Cart", iteration + 1,
                scenarioResults, clickCount
            )

            if (iteration < SHORT_ITERATIONS - 1) Thread.sleep(2000)
        }

        finalizeScenarioResults("Shopping_Cart", scenarioResults)
    }

    @Test
    fun chatStreamingEnergy() {
        val scenarioResults = mutableListOf<EnergyHelper.IterationResult>()

        repeat(ITERATIONS) { iteration ->
            println("=".repeat(40))
            println("Chat Streaming - Iteration ${iteration + 1}/$ITERATIONS")
            println("=".repeat(40))

            var energyBefore: EnergyHelper.EnergyMeasurement? = null
            var energyAfter: EnergyHelper.EnergyMeasurement? = null
            var scrollCount = 0

            benchmarkRule.measureRepeated(
                packageName = BenchmarkHelper.PACKAGE_NAME,
                metrics = listOf(FrameTimingMetric()),
                compilationMode = CompilationMode.DEFAULT,
                iterations = 1,
                startupMode = StartupMode.WARM,
                setupBlock = {
                    pressHome()
                    startActivityAndWait()
                    device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 5000)
                    device.wait(Until.hasObject(By.scrollable(true)), 3000)
                    Thread.sleep(500)

                    navigateToMessageBox(device)
                    device.wait(Until.hasObject(By.scrollable(true)), 2000)
                    Thread.sleep(500)

                    val screenHeight = device.displayHeight
                    val screenWidth = device.displayWidth
                    device.click(screenWidth / 2, screenHeight / 4)

                    device.wait(Until.hasObject(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).clazz("android.widget.EditText")
                    ), 2000)
                    Thread.sleep(500)

                    EnergyHelper.waitForStabilization(STABILIZATION_SECONDS)
                    energyBefore = EnergyHelper.captureEnergySnapshot()
                }
            ) {
                val endTime = System.currentTimeMillis() + LONG_TEST_DURATION

                while (System.currentTimeMillis() < endTime) {
                    if (scrollCount % 8 == 0) {
                        device.findObjects(
                            By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                        ).maxByOrNull { it.visibleBounds.height() }
                            ?.scroll(Direction.DOWN, 0.3f)
                    }
                    Thread.sleep(1000)
                    scrollCount++
                }

                energyAfter = EnergyHelper.captureEnergySnapshot()
            }

            processIterationResult(
                energyBefore, energyAfter,
                "Chat_Streaming", iteration + 1,
                scenarioResults, scrollCount
            )

            if (iteration < ITERATIONS - 1) Thread.sleep(2000)
        }

        finalizeScenarioResults("Chat_Streaming", scenarioResults)
    }

    @SuppressLint("DefaultLocale")
    private fun processIterationResult(
        before: EnergyHelper.EnergyMeasurement?,
        after: EnergyHelper.EnergyMeasurement?,
        scenarioName: String,
        iteration: Int,
        results: MutableList<EnergyHelper.IterationResult>,
        operationCount: Int
    ) {
        if (before != null && after != null) {
            val isValid = EnergyHelper.validateMeasurement(
                before, after, MIN_CHARGE_CHANGE_MAH
            )

            val result = EnergyHelper.calculateEnergyConsumption(
                before, after, "${scenarioName}_Iter${iteration}"
            )

            val state = EnergyHelper.captureDeviceState()
            val iterResult = EnergyHelper.IterationResult(
                iteration = iteration,
                energyMWh = result.energyConsumedMWh,
                chargeMah = result.chargeConsumedMAh,
                powerMW = result.averagePowerMW,
                durationSec = result.durationSeconds,
                validMeasurement = isValid,
                batteryLevel = state.batteryLevel,
                temperature = state.batteryTemperature,
                operationCount = operationCount
            )

            results.add(iterResult)

            println("Energy: ${String.format("%.2f", result.energyConsumedMWh)} mWh " +
                    "(${if (isValid) "✅ Valid" else "⚠️ Low"})")

            Log.i(TAG, "Iteration $iteration: " +
                    "${String.format("%.2f", result.energyConsumedMWh)} mWh")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun finalizeScenarioResults(
        scenarioName: String,
        results: List<EnergyHelper.IterationResult>
    ) {
        if (results.isNotEmpty()) {
            val validResults = results.filter { it.validMeasurement }

            if (validResults.isEmpty()) {
                Log.w(TAG, "⚠️ No valid measurements for $scenarioName")
                return
            }

            val energyValues = validResults.map { it.energyMWh }.sorted()
            val median = energyValues[energyValues.size / 2]
            val stats = EnergyHelper.calculateStatistics(energyValues)

            val medianResult = validResults.sortedBy { it.energyMWh }[validResults.size / 2]

            energyResults.add(
                EnergyHelper.EnergyResult(
                    energyConsumedMWh = median,
                    chargeConsumedMAh = medianResult.chargeMah,
                    averagePowerMW = medianResult.powerMW,
                    durationSeconds = medianResult.durationSec,
                    scenarioName = scenarioName,
                    usedVoltageMethod = false,
                    statistics = stats
                )
            )

            EnergyHelper.saveDetailedResults(scenarioName, results)

            println("\n✅ $scenarioName RESULTS:")
            println("   Median: ${String.format("%.2f", median)} mWh")
            println("   Mean: ${String.format("%.2f", stats.mean)} ± " +
                    "${String.format("%.2f", stats.confidenceInterval95.second - stats.mean)} mWh (95% CI)")
            println("   CV: ${String.format("%.1f", stats.coefficientOfVariation)}%")
            println("   Valid: ${validResults.size}/$ITERATIONS\n")
        }
    }

    private fun navigateToCartSafely(device: androidx.test.uiautomator.UiDevice) {
        device.findObject(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Shopping Cart")
        )?.click()
        device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 2000)
        Thread.sleep(500)
    }

    private fun clearCart(device: UiDevice) {
        var attempts = 0

        while (attempts < 50) {
            val removeButtons = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Remove")
            )

            if (removeButtons.isEmpty()) {
                break
            }

            try {
                val buttonToRemove = removeButtons.firstOrNull()
                if (buttonToRemove != null) {
                    buttonToRemove.click()
                    device.waitForIdle(200)
                    attempts++
                } else {
                    break
                }
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(100)
                attempts++
            } catch (e: Exception) {
                attempts++
            }
        }
    }
    private fun navigateToMessageBox(device: androidx.test.uiautomator.UiDevice) {
        device.findObject(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Message Box")
        )?.click()
        device.wait(
            Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)),
            2000
        )
    }

    private fun ensureOnProductList(device: androidx.test.uiautomator.UiDevice) {
        var attempts = 0
        while (!device.hasObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ) && attempts < 3
        ) {
            device.pressBack()
            device.wait(Until.hasObject(By.pkg(BenchmarkHelper.PACKAGE_NAME)), 1000)
            attempts++
        }
    }

    private fun addProductsToCart(device: androidx.test.uiautomator.UiDevice, count: Int) {
        val screenHeight = device.displayHeight
        val screenWidth = device.displayWidth
        val productClickX = screenWidth / 4
        val productClickY = screenHeight / 2

        for (i in 1..count) {
            try {
                device.click(productClickX, productClickY)
                Thread.sleep(200)

                device.findObject(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).text("Add to Cart")
                )?.click()
                Thread.sleep(100)

                device.pressBack()
                Thread.sleep(100)

                device.findObjects(
                    By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                ).maxByOrNull { it.visibleBounds.height() }?.let { grid ->
                    repeat(4) {
                        try {
                            grid.scroll(Direction.DOWN, 5.0f)
                            Thread.sleep(50)
                        } catch (e: Exception) {
                            //ignore
                        }
                    }
                }
            } catch (e: Exception) {
                //ignore
            }
        }
    }
}