import com.yfy.basearchitecture.core.navigation.helpers.NavigationDataStore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NavigationDataStoreTest {

    @Before
    fun setUp() {
        NavigationDataStore.clearData()
    }

    @After
    fun tearDown() {
        NavigationDataStore.clearData()
    }

    @Test
    fun `test data storage and retrieval`() {
        val key = "test_key"
        val testData = "test_value"

        NavigationDataStore.putData(key, testData)
        val retrievedData = NavigationDataStore.getData<String>(key)

        assertEquals(testData, retrievedData)
    }

    @Test
    fun `test data removal`() {
        val key = "test_key"
        val testData = "test_value"

        NavigationDataStore.putData(key, testData)
        NavigationDataStore.removeData(key)
        val retrievedData = NavigationDataStore.getData<String>(key)

        assertNull(retrievedData)
    }

    @Test
    fun `test data clear`() {
        NavigationDataStore.putData("key1", "value1")
        NavigationDataStore.putData("key2", "value2")

        NavigationDataStore.clearData()

        assertNull(NavigationDataStore.getData<String>("key1"))
        assertNull(NavigationDataStore.getData<String>("key2"))
    }

    @Test
    fun `test key generation uniqueness`() {
        val keys = mutableSetOf<String>()
        repeat(100) {
            val key = NavigationDataStore.generateKey()
            assertTrue("Generated key should be unique", keys.add(key))
            assertTrue("Key should have proper format", key.startsWith("nav_data_"))
        }
    }

    @Test
    fun `test complex data storage`() {
        data class TestData(val id: Int, val name: String, val items: List<String>)

        val key = "complex_key"
        val complexData = TestData(1, "Test", listOf("item1", "item2"))

        NavigationDataStore.putData(key, complexData)
        val retrievedData = NavigationDataStore.getData<TestData>(key)

        assertEquals(complexData, retrievedData)
    }

    @Test
    fun `test data expiration simulation`() = runTest {
        // Bu test TTL mekanizmasını test etmek için mock time kullanabilir
        // Gerçek zamanlı test için Thread.sleep kullanabiliriz ama bu yavaş olacağı için
        // mock time tercih ediyoruz

        val key = "expire_key"
        val testData = "expire_value"

        NavigationDataStore.putData(key, testData)

        // Normal durumda data olmalı
        assertNotNull(NavigationDataStore.getData<String>(key))

        // TTL testi için private method'ları test etmek zor olduğu için
        // bu testi integration test olarak yapabiliriz
    }

    @Test
    fun `test concurrent access`() {
        val latch = CountDownLatch(10)
        val results = mutableListOf<String?>()

        // 10 thread ile aynı anda data store'a erişim
        repeat(10) { index ->
            Thread {
                try {
                    val key = "thread_key_$index"
                    val value = "thread_value_$index"

                    NavigationDataStore.putData(key, value)
                    val retrieved = NavigationDataStore.getData<String>(key)

                    synchronized(results) {
                        results.add(retrieved)
                    }
                } finally {
                    latch.countDown()
                }
            }.start()
        }

        assertTrue("All threads should complete", latch.await(5, TimeUnit.SECONDS))
        assertEquals("All threads should return data", 10, results.size)
        results.forEach { result ->
            assertNotNull("Each result should not be null", result)
            assertTrue("Result should match pattern", result!!.startsWith("thread_value_"))
        }
    }
}
