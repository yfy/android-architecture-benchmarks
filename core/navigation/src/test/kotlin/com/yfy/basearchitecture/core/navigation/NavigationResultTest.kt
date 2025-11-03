import com.yfy.basearchitecture.core.navigation.helpers.NavigationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NavigationResultTest {

    @Test
    fun `test success result`() {
        val result = NavigationResult.Success
        assertTrue("Result should be Success", result is NavigationResult.Success)
    }

    @Test
    fun `test error result with message`() {
        val errorMessage = "Test error message"
        val result = NavigationResult.Error(errorMessage)

        assertTrue("Result should be Error", result is NavigationResult.Error)
        assertEquals(errorMessage, result.message)
        assertNull("Throwable should be null", result.throwable)
    }

    @Test
    fun `test error result with throwable`() {
        val errorMessage = "Test error message"
        val throwable = RuntimeException("Test exception")
        val result = NavigationResult.Error(errorMessage, throwable)

        assertTrue("Result should be Error", result is NavigationResult.Error)
        assertEquals(errorMessage, result.message)
        assertEquals(throwable, result.throwable)
    }
}
