package com.yfy.basearchitecture.benchmark.energy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.yfy.basearchitecture.benchmark.utils.ArchitectureConfig
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

object EnergyHelper {

    private const val TAG = "EnergyHelper"

    private val batteryManager: BatteryManager by lazy {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    private val context: Context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private val architecture = ArchitectureConfig.CURRENT_ARCHITECTURE.getShortName()

    data class EnergyMeasurement(
        val energyNWh: Long,
        val chargeUAh: Int,
        val voltageUV: Int,
        val timestamp: Long
    )

    data class EnergyResult(
        val energyConsumedMWh: Double,
        val chargeConsumedMAh: Double,
        val averagePowerMW: Double,
        val durationSeconds: Double,
        val scenarioName: String,
        val usedVoltageMethod: Boolean,
        val statistics: StatisticalSummary? = null
    )

    data class IterationResult(
        val iteration: Int,
        val energyMWh: Double,
        val chargeMah: Double,
        val powerMW: Double,
        val durationSec: Double,
        val validMeasurement: Boolean,
        val batteryLevel: Int,
        val temperature: Float,
        val operationCount: Int
    )

    data class DeviceState(
        val batteryLevel: Int,
        val batteryTemperature: Float,
        val screenBrightness: Int,
        val isAirplaneMode: Boolean,
        val timestamp: Long
    )

    data class StatisticalSummary(
        val mean: Double,
        val median: Double,
        val stdDev: Double,
        val min: Double,
        val max: Double,
        val coefficientOfVariation: Double,
        val confidenceInterval95: Pair<Double, Double>
    )

    fun captureEnergySnapshot(): EnergyMeasurement {
        return EnergyMeasurement(
            energyNWh = try {
                batteryManager.getLongProperty(
                    BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER
                )
            } catch (e: Exception) {
                0L
            },
            chargeUAh = batteryManager.getIntProperty(
                BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
            ),
            voltageUV = getBatteryVoltageUV(),
            timestamp = System.currentTimeMillis()
        )
    }

    private fun getBatteryVoltageUV(): Int {
        val batteryStatus: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        return if (voltage > 0) voltage * 1000 else 0
    }

    fun calculateEnergyConsumption(
        before: EnergyMeasurement,
        after: EnergyMeasurement,
        scenarioName: String
    ): EnergyResult {
        val durationMs = after.timestamp - before.timestamp
        val durationSec = durationMs / 1000.0
        val chargeConsumedMAh = (before.chargeUAh - after.chargeUAh) / 1000.0

        val useVoltageMethod = before.energyNWh == 0L || after.energyNWh == 0L ||
                (before.energyNWh - after.energyNWh) == 0L

        val energyConsumedMWh = if (useVoltageMethod) {
            val avgVoltageUV = (before.voltageUV + after.voltageUV) / 2.0
            val chargeConsumedUAh = (before.chargeUAh - after.chargeUAh).toDouble()
            val energyNWh = (chargeConsumedUAh * avgVoltageUV) / 1000.0
            energyNWh / 1_000_000.0
        } else {
            (before.energyNWh - after.energyNWh) / 1_000_000.0
        }

        val averagePowerMW = if (durationSec > 0) {
            (energyConsumedMWh * 3600) / durationSec
        } else 0.0

        return EnergyResult(
            energyConsumedMWh = energyConsumedMWh,
            chargeConsumedMAh = chargeConsumedMAh,
            averagePowerMW = averagePowerMW,
            durationSeconds = durationSec,
            scenarioName = scenarioName,
            usedVoltageMethod = useVoltageMethod
        )
    }

    fun validateMeasurement(
        before: EnergyMeasurement,
        after: EnergyMeasurement,
        minExpectedChargeChange: Double
    ): Boolean {
        val chargeChangeUAh = before.chargeUAh - after.chargeUAh
        val chargeChangeMAh = chargeChangeUAh / 1000.0

        if (chargeChangeMAh < minExpectedChargeChange) {
            Log.w(TAG, """
                ⚠️ LOW CHARGE CHANGE: ${String.format("%.2f", chargeChangeMAh)} mAh
                Expected minimum: $minExpectedChargeChange mAh
                Recommendation: Increase test duration or intensity
            """.trimIndent())
            return false
        }

        return true
    }

    fun calculateStatistics(values: List<Double>): StatisticalSummary {
        val sorted = values.sorted()
        val mean = values.average()
        val median = sorted[sorted.size / 2]

        val variance = values.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        val cv = (stdDev / mean) * 100

        val n = values.size
        val tValue = 2.145
        val standardError = stdDev / sqrt(n.toDouble())
        val marginOfError = tValue * standardError

        return StatisticalSummary(
            mean = mean,
            median = median,
            stdDev = stdDev,
            min = sorted.first(),
            max = sorted.last(),
            coefficientOfVariation = cv,
            confidenceInterval95 = Pair(mean - marginOfError, mean + marginOfError)
        )
    }

    fun captureDeviceState(): DeviceState {
        val batteryStatus = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        return DeviceState(
            batteryLevel = batteryStatus?.getIntExtra(
                BatteryManager.EXTRA_LEVEL, -1
            ) ?: -1,
            batteryTemperature = batteryStatus?.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE, -1
            )?.div(10f) ?: -1f,
            screenBrightness = try {
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS
                )
            } catch (e: Exception) { -1 },
            isAirplaneMode = try {
                Settings.Global.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
                ) == 1
            } catch (e: Exception) { false },
            timestamp = System.currentTimeMillis()
        )
    }

    fun waitForStabilization(seconds: Int) {
        Thread.sleep(seconds * 1000L)
    }

    fun isUnplugged(): Boolean {
        val plugType = batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_STATUS
        )
        return plugType == BatteryManager.BATTERY_STATUS_DISCHARGING
    }

    fun isEnergyCounterSupported(): Boolean {
        return try {
            val snapshot = captureEnergySnapshot()
            snapshot.energyNWh > 0
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("DefaultLocale")
    fun saveEnergyResults(results: List<EnergyResult>) {
        if (results.isEmpty()) {
            Log.w(TAG, "No energy results to save")
            return
        }

        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date())
        val csvFile = File(resultsDir, "energy_consumption_${timestamp}.csv")
        val jsonFile = File(resultsDir, "energy_consumption_${timestamp}.json")
        val readmeFile = File(resultsDir, "README_${timestamp}.txt")

        saveCsvResults(csvFile, results)
        saveJsonResults(jsonFile, results)
        saveReadme(readmeFile, results)

        println("\n✅ Energy results saved:")
        println("   Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
        println("   CSV: ${csvFile.name}")
        println("   JSON: ${jsonFile.name}")
        println("   README: ${readmeFile.name}")
    }

    @SuppressLint("DefaultLocale")
    fun saveDetailedResults(
        scenarioName: String,
        iterations: List<IterationResult>
    ) {
        val resultsDir = getResultsDir()
        val timestamp = dateFormat.format(Date())
        val file = File(resultsDir, "detailed_${scenarioName}_${timestamp}.csv")

        file.printWriter().use { out ->
            out.println("Iteration,Energy_mWh,Charge_mAh,Power_mW,Duration_Sec," +
                    "Valid,Battery_%,Temp_C,Operations")

            iterations.forEach { iter ->
                out.println("${iter.iteration}," +
                        "${String.format("%.4f", iter.energyMWh)}," +
                        "${String.format("%.4f", iter.chargeMah)}," +
                        "${String.format("%.4f", iter.powerMW)}," +
                        "${String.format("%.2f", iter.durationSec)}," +
                        "${iter.validMeasurement}," +
                        "${iter.batteryLevel}," +
                        "${String.format("%.1f", iter.temperature)}," +
                        "${iter.operationCount}")
            }
        }
    }

    private fun getResultsDir(): File {
        val externalDir = context.getExternalFilesDir(null) ?: context.filesDir
        val baseDir = File(externalDir, "benchmark_results/$architecture")
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
        return baseDir
    }

    @SuppressLint("DefaultLocale")
    private fun saveCsvResults(file: File, results: List<EnergyResult>) {
        val usingVoltageMethod = results.any { it.usedVoltageMethod }

        file.printWriter().use { out ->
            out.println("# ${"=".repeat(48)}")
            out.println("# ENERGY CONSUMPTION TEST RESULTS")
            out.println("# ${"=".repeat(48)}")
            out.println("#")
            out.println("# Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}")
            out.println("# Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
            out.println("# Device: ${android.os.Build.MODEL}")
            out.println("# Package: ${BenchmarkHelper.PACKAGE_NAME}")
            out.println("# Test Count: ${results.size}")
            out.println("#")

            if (usingVoltageMethod) {
                out.println("# METHOD: Charge × Voltage")
                out.println("#   Energy = Charge (µAh) × Voltage (µV) / 10^9")
            } else {
                out.println("# METHOD: Hardware Energy Counter")
            }

            out.println("#")
            out.println("# ${"=".repeat(48)}")
            out.println()

            out.println("Scenario,Energy_mWh,Charge_mAh,Avg_Power_mW,Duration_Sec")

            results.forEach { result ->
                out.println("${result.scenarioName}," +
                        "${String.format("%.4f", result.energyConsumedMWh)}," +
                        "${String.format("%.4f", result.chargeConsumedMAh)}," +
                        "${String.format("%.4f", result.averagePowerMW)}," +
                        "${String.format("%.2f", result.durationSeconds)}")
            }

            out.println()
            out.println("# ${"=".repeat(48)}")
            out.println("# STATISTICAL SUMMARY")
            out.println("# ${"=".repeat(48)}")

            results.forEach { result ->
                result.statistics?.let { stats ->
                    out.println("#")
                    out.println("# ${result.scenarioName}:")
                    out.println("#   Mean: ${String.format("%.2f", stats.mean)} ± " +
                            "${String.format("%.2f", stats.confidenceInterval95.second - stats.mean)} mWh (95% CI)")
                    out.println("#   Median: ${String.format("%.2f", stats.median)} mWh")
                    out.println("#   Std Dev: ${String.format("%.2f", stats.stdDev)} mWh")
                    out.println("#   CV: ${String.format("%.1f", stats.coefficientOfVariation)}%")
                    out.println("#   Range: ${String.format("%.2f", stats.min)} - " +
                            "${String.format("%.2f", stats.max)} mWh")
                }
            }

            val totalEnergy = results.sumOf { it.energyConsumedMWh }
            val avgPower = results.map { it.averagePowerMW }.average()

            out.println("#")
            out.println("# Total Energy: ${String.format("%.2f", totalEnergy)} mWh")
            out.println("# Average Power: ${String.format("%.2f", avgPower)} mW")
            out.println("#")
            out.println("# ${"=".repeat(48)}")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun saveJsonResults(file: File, results: List<EnergyResult>) {
        val usingVoltageMethod = results.any { it.usedVoltageMethod }

        val json = buildString {
            appendLine("{")
            appendLine("""  "architecture": "$architecture",""")
            appendLine("""  "architecture_full": "${ArchitectureConfig.getCurrentArchitectureInfo()}",""")
            appendLine("""  "timestamp": "${System.currentTimeMillis()}",""")
            appendLine("""  "timestamp_readable": "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}",""")
            appendLine("""  "device": "${android.os.Build.MODEL}",""")
            appendLine("""  "package": "${BenchmarkHelper.PACKAGE_NAME}",""")
            appendLine("""  "test_count": ${results.size},""")
            appendLine("""  "measurement_method": "${if (usingVoltageMethod) "charge_voltage" else "energy_counter"}",""")
            appendLine("""  "results": [""")

            results.forEachIndexed { index, result ->
                appendLine("    {")
                appendLine("""      "scenario": "${result.scenarioName}",""")
                appendLine("""      "energy_mWh": ${String.format("%.4f", result.energyConsumedMWh)},""")
                appendLine("""      "charge_mAh": ${String.format("%.4f", result.chargeConsumedMAh)},""")
                appendLine("""      "average_power_mW": ${String.format("%.4f", result.averagePowerMW)},""")
                appendLine("""      "duration_sec": ${String.format("%.2f", result.durationSeconds)}""")

                result.statistics?.let { stats ->
                    appendLine(",")
                    appendLine("""      "statistics": {""")
                    appendLine("""        "mean": ${String.format("%.4f", stats.mean)},""")
                    appendLine("""        "median": ${String.format("%.4f", stats.median)},""")
                    appendLine("""        "std_dev": ${String.format("%.4f", stats.stdDev)},""")
                    appendLine("""        "cv": ${String.format("%.2f", stats.coefficientOfVariation)},""")
                    appendLine("""        "min": ${String.format("%.4f", stats.min)},""")
                    appendLine("""        "max": ${String.format("%.4f", stats.max)},""")
                    appendLine("""        "ci_95_lower": ${String.format("%.4f", stats.confidenceInterval95.first)},""")
                    appendLine("""        "ci_95_upper": ${String.format("%.4f", stats.confidenceInterval95.second)}""")
                    appendLine("""      }""")
                }

                appendLine("    }${if (index < results.size - 1) "," else ""}")
            }

            appendLine("  ],")

            val totalEnergy = results.sumOf { it.energyConsumedMWh }
            val totalDuration = results.sumOf { it.durationSeconds }
            val avgPower = results.map { it.averagePowerMW }.average()

            appendLine("""  "summary": {""")
            appendLine("""    "total_energy_mWh": ${String.format("%.4f", totalEnergy)},""")
            appendLine("""    "total_duration_sec": ${String.format("%.2f", totalDuration)},""")
            appendLine("""    "average_power_mW": ${String.format("%.4f", avgPower)}""")
            appendLine("  }")
            appendLine("}")
        }

        file.writeText(json)
    }

    @SuppressLint("DefaultLocale")
    private fun saveReadme(file: File, results: List<EnergyResult>) {
        val deviceState = captureDeviceState()

        file.writeText("""
ANDROID ARCHITECTURE ENERGY BENCHMARK RESULTS
${"═".repeat(60)}

TEST INFORMATION
${"─".repeat(60)}
Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}
Device: ${android.os.Build.MODEL} (${android.os.Build.DEVICE})
Android: ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})
Architecture: ${ArchitectureConfig.getCurrentArchitectureInfo()}

MEASUREMENT METHOD
${"─".repeat(60)}
${if (results.any { it.usedVoltageMethod })
            "Charge × Voltage (Software-based)\nEnergy = Charge (µAh) × Voltage (µV) / 10^9"
        else "Hardware Energy Counter"}

DEVICE CONDITIONS
${"─".repeat(60)}
Battery Level: ${deviceState.batteryLevel}%
Temperature: ${String.format("%.1f", deviceState.batteryTemperature)}°C
Screen Brightness: ${deviceState.screenBrightness}
Airplane Mode: ${if (deviceState.isAirplaneMode) "Enabled" else "Disabled"}

TEST SCENARIOS
${"─".repeat(60)}
1. Product Browsing: 60s continuous scroll + category filtering
2. Shopping Cart: 120s rapid quantity updates (15 items)
3. Chat Streaming: 60s message streaming observation

Each scenario: 15 iterations, 10s stabilization

RESULTS SUMMARY
${"─".repeat(60)}
${results.joinToString("\n") { result ->
            "${result.scenarioName}: ${String.format("%.2f", result.energyConsumedMWh)} mWh"
        }}

Total Energy: ${String.format("%.2f", results.sumOf { it.energyConsumedMWh })} mWh
Average Power: ${String.format("%.2f", results.map { it.averagePowerMW }.average())} mW

DATA FILES
${"─".repeat(60)}
- energy_consumption_*.csv   Summary results
- detailed_*_*.csv           Per-iteration data
- energy_consumption_*.json  Machine-readable format
- README_*.txt               This file

NOTES
${"─".repeat(60)}
- Device unplugged during all measurements
- Results represent median of 15 iterations
- Charge counter resolution: ~1 mAh (Samsung limitation)
- For statistical analysis, use detailed CSV files

${"═".repeat(60)}
        """.trimIndent())
    }
}