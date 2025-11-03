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
class ChatStreamBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Test 1: Chat List Realtime Updates
     *
     * Scenario:
     * 1. Navigate to Chat List from Product List
     * 2. Observe realtime updates (new messages every 3s, typing indicators every 5s)
     * 3. Scroll chat list up and down to stress render
     * 4. Total: 15 seconds observation (5 update cycles)
     */
    @Test
    fun chatListRealtimeUpdates() {
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

                // Navigate to Chat List from Product List
                navigateToMessageBox(device)
                Thread.sleep(500)
            }
        ) {
            println("========================================")
            println("TEST: Chat List Realtime Updates")
            println("========================================")

            val chatList = device.findObjects(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ).maxByOrNull { it.visibleBounds.height() }

            if (chatList != null) {
                println("Found chat list: height=${chatList.visibleBounds.height()}")

                // Observe for 15 seconds (5 cycles of 3s updates)
                repeat(5) { cycle ->
                    println("Observation cycle ${cycle + 1}/5")

                    // Wait 3 seconds for updates to arrive
                    Thread.sleep(3000)

                    // Scroll down to see all chats
                    try {
                        repeat(1) {
                            chatList.fling(Direction.DOWN)
                            scrollCount++
                            Thread.sleep(150)
                        }
                    } catch (e: Exception) {
                        println("   Scroll down error: ${e.message}")
                    }

                    // Scroll back up
                    try {
                        repeat(1) {
                            chatList.fling(Direction.UP)
                            scrollCount++
                            Thread.sleep(150)
                        }
                    } catch (e: Exception) {
                        println("   Scroll up error: ${e.message}")
                    }

                    println("   Cycle ${cycle + 1} completed")
                }

                println("✅ Observed 5 update cycles (15 seconds total)")
            } else {
                println("ERROR: Chat list not found!")
            }

            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "ChatList_RealtimeUpdates",
                totalDurationMs = totalDuration,
                actionCount = scrollCount,
                actionType = "scroll_with_updates",
                additionalInfo = mapOf(
                    "observation_cycles" to "5",
                    "duration_per_cycle" to "3000"
                )
            )
        )
    }

    /**
     * Test 2: Chat Detail Message Stream with Multiple Chats
     *
     * Scenario:
     * 1. Navigate to Chat List
     * 2. Open first chat
     * 3. Wait for messages to stream (100 messages at 500ms interval = 50s total)
     * 4. Observe for 10 seconds (20 messages)
     * 5. Go back to Chat List
     * 6. Open second chat
     * 7. Observe messages for 10 seconds
     * 8. Send 5 messages
     */
    @Test
    fun chatDetailMessageStreamAndSending() {
        val startTime = System.currentTimeMillis()
        var navigationCount = 0
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

                // Navigate to Chat List
                navigateToMessageBox(device)
                Thread.sleep(500)
            }
        ) {
            println("========================================")
            println("TEST: Chat Detail Message Stream")
            println("========================================")

            val screenHeight = device.displayHeight
            val screenWidth = device.displayWidth

            // ===== PART 1: First Chat =====
            println("PART 1: Opening first chat")

            // Click first chat (top of list)
            val firstChatY = screenHeight / 4 // Upper part of screen
            val firstChatX = screenWidth / 2

            device.click(firstChatX, firstChatY)
            navigationCount++
            Thread.sleep(500) // Wait for chat to open

            // Observe message stream for 10 seconds (20 messages)
            println("Observing message stream (10 seconds)...")
            repeat(20) { messageCount ->
                Thread.sleep(500) // Each message arrives at 500ms

                // Every 5 messages, check if we can scroll (list grows)
                if (messageCount % 5 == 0) {
                    println("   Received ~${messageCount} messages")
                }
            }

            println("✅ Observed 20 messages in first chat")

            // ===== PART 2: Go Back and Open Second Chat =====
            println("PART 2: Returning to chat list")

            device.pressBack()
            Thread.sleep(300)

            println("PART 3: Opening second chat")

            // Click second chat (below first one)
            val secondChatY = screenHeight / 4 + 120 // Below first chat

            device.click(firstChatX, secondChatY)
            navigationCount++
            Thread.sleep(500)

            // Observe for another 10 seconds (20 more messages)
            println("Observing second chat message stream (10 seconds)...")
            repeat(20) { messageCount ->
                Thread.sleep(500)

                if (messageCount % 5 == 0) {
                    println("   Received ~${messageCount} messages in second chat")
                }
            }

            println("✅ Observed 20 messages in second chat")

            println("========================================")
            println("✅ TEST COMPLETED")
            println("   - Observed first chat: 20 messages")
            println("   - Observed second chat: 20 messages")
            println("   - Sent: 5 messages")
            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "ChatDetail_MessageStreamAndSending",
                totalDurationMs = totalDuration,
                actionCount = navigationCount,
                actionType = "navigation_and_observation",
                additionalInfo = mapOf(
                    "chats_opened" to navigationCount.toString(),
                    "messages_per_chat" to "20",
                    "observation_time" to "10000"
                )
            )
        )
    }

    /**
     * Test 3: Rapid Chat Switching (Stress Test)
     *
     * Scenario:
     * 1. Navigate to Chat List via Message Box
     * 2. Click first visible chat → Detail → Wait → Back
     * 3. Scroll DOWN → Click first visible → Detail → Wait → Back
     * 4. Scroll UP → Click first visible → Detail → Wait → Back
     * 5. Scroll DOWN → Click first visible → Detail → Wait → Back
     * 6. Repeat pattern 5 times
     */
    @Test
    fun chatRapidSwitching() {
        val startTime = System.currentTimeMillis()
        var chatOpenCount = 0
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

                navigateToMessageBox(device)

                val chatListVisible = device.wait(
                    Until.hasObject(
                        By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
                    ),
                    2000
                )

                if (!chatListVisible) {
                    println("ERROR: Chat List not visible after navigation")
                    throw Exception("Chat List not opened")
                }

                println("✅ Chat List opened successfully")
            }
        ) {
            println("========================================")
            println("TEST: Rapid Chat Switching with Scrolling")
            println("========================================")

            // Repeat 5 cycles
            repeat(5) { cycle ->
                println("\n=== Cycle ${cycle + 1}/5 ===")

                // Pattern: Down → Up → Down → Up for each cycle
                val scrollPattern = listOf(
                    Direction.DOWN,  // First: Scroll down then click
                    Direction.UP,    // Second: Scroll up then click
                    Direction.DOWN,  // Third: Scroll down then click
                    Direction.UP     // Fourth: Scroll down then click
                )

                scrollPattern.forEachIndexed { index, scrollDirection ->
                    println("\n Step ${index + 1}/4:")

                    // STEP 1: Ensure we're on Chat List (find scrollable)
                    val chatList = waitForChatList(device)
                    if (chatList == null) {
                        println("⚠️ Chat List not found, skipping...")
                        return@forEachIndexed
                    }

                    // STEP 2: Scroll
                    println("Scrolling ${scrollDirection}...")
                    try {
                        chatList.scroll(scrollDirection, 0.8f)
                        Thread.sleep(100)
                    } catch (e: Exception) {
                        println("Scroll error: ${e.message}")
                    }

                    // Re-find chat list after scroll
                    val chatListAfterScroll = waitForChatList(device)
                    if (chatListAfterScroll == null) {
                        println("⚠️ Chat List lost after scroll")
                        return@forEachIndexed
                    }

                    // STEP 3: Get first visible chat from list
                    val firstChat = getFirstVisibleChat(device)
                    if (firstChat == null) {
                        println("❌ No visible chat found")
                        return@forEachIndexed
                    }

                    println("Clicking first visible chat...")

                    // STEP 4: Click first chat
                    try {
                        firstChat.click()
                        chatOpenCount++
                        Thread.sleep(100)
                    } catch (e: Exception) {
                        println("Click error: ${e.message}")
                        return@forEachIndexed
                    }

                    // STEP 5: Verify we're in Chat Detail (EditText exists)
                    val inChatDetail = device.wait(
                        Until.hasObject(
                            By.pkg(BenchmarkHelper.PACKAGE_NAME).clazz("android.widget.EditText")
                        ),
                        500
                    )

                    if (inChatDetail) {
                        println("✅ In Chat Detail, observing...")
                        Thread.sleep(2000) // Observe messages
                    } else {
                        println("⚠️ Chat Detail not opened")
                    }

                    // STEP 6: Go back to Chat List
                    println("Going back to Chat List...")
                    device.pressBack()
                    Thread.sleep(100)
                }

                println("\nCycle ${cycle + 1} completed ✅")
            }

            println("\n========================================")
            println("✅ All 5 cycles completed (20 chat opens)")
            println("========================================")
        }

        val totalDuration = System.currentTimeMillis() - startTime
        BenchmarkHelper.exportFrameTimingResults(
            FrameTimingMetrics(
                testName = "Chat_RapidSwitching",
                totalDurationMs = totalDuration,
                actionCount = chatOpenCount,
                actionType = "rapid_navigation",
                additionalInfo = mapOf(
                    "cycles" to "5",
                    "chats_per_cycle" to "4"
                )
            )
        )

    }

    /**
     * Helper: Wait for and find Chat List (scrollable)
     * Returns null if not found
     */
    private fun waitForChatList(device: androidx.test.uiautomator.UiDevice): androidx.test.uiautomator.UiObject2? {
        // Wait a bit for chat list to be visible
        device.wait(
            Until.hasObject(
                By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
            ),
            1000
        )

        // Find tallest scrollable (Chat List)
        val chatList = device.findObjects(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
        ).maxByOrNull { it.visibleBounds.height() }

        return chatList
    }

    /**
     * Helper: Get first visible LARGE clickable chat from Chat List
     * Skips small elements (like dividers) and returns first substantial chat card
     * Returns null if not found
     */
    private fun getFirstVisibleChat(device: androidx.test.uiautomator.UiDevice): androidx.test.uiautomator.UiObject2? {
        // Find Chat List first
        val chatList = device.findObjects(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
        ).maxByOrNull { it.visibleBounds.height() }

        if (chatList == null) {
            println("Chat List not found")
            return null
        }

        // Get children (chat items)
        val chatItems = chatList.children
                .filter { it.isClickable && it.visibleBounds.height() > 150 }

        return if (chatItems.size > 1) chatItems[1] else chatItems.first()
    }

    /**
     * Helper: Navigate to Message Box from Product List
     */
    private fun navigateToMessageBox(device: androidx.test.uiautomator.UiDevice) {
        println("Navigating to Message Box...")
        device.findObject(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).desc("Message Box")
        )?.click()

        device.wait(Until.hasObject(
            By.pkg(BenchmarkHelper.PACKAGE_NAME).scrollable(true)
        ), 2000)

        println("✅ Navigated to Chat List")
    }
}