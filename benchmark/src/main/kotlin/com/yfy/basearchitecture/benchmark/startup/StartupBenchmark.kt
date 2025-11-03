package com.yfy.basearchitecture.benchmark.startup

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.yfy.basearchitecture.benchmark.model.StartupMetrics
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper
import com.yfy.basearchitecture.benchmark.utils.BenchmarkHelper.STARTUP_ITERATIONS
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCold() {
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = STARTUP_ITERATIONS,
            startupMode = StartupMode.COLD,
            setupBlock = {
                pressHome()
                Thread.sleep(1000)
            }
        ) {
            startActivityAndWait()

            device.wait(Until.hasObject(By.scrollable(true)), 5000)
        }

        BenchmarkHelper.exportStartupResults(
            StartupMetrics(
                testName = "Cold_Startup",
                iterations = STARTUP_ITERATIONS,
                startupMode = "COLD"
            )
        )
    }

    @Test
    fun startupWarm() {
        benchmarkRule.measureRepeated(
            packageName = BenchmarkHelper.PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = CompilationMode.DEFAULT,
            iterations = STARTUP_ITERATIONS,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                Thread.sleep(500)
            }
        ) {
            startActivityAndWait()
            device.wait(Until.hasObject(By.scrollable(true)), 3000)
        }

        BenchmarkHelper.exportStartupResults(
            StartupMetrics(
                testName = "Warm_Startup",
                iterations = STARTUP_ITERATIONS,
                startupMode = "WARM"
            )
        )
    }

}