import com.yfy.basearchitecture.core.navigation.helpers.NavigationData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NavigationDataTest {

    @Test
    fun `test navigation data creation`() {
        val dataKey = "test_key"
        val extras = mapOf("param1" to "value1", "param2" to "value2")

        val navigationData = NavigationData(dataKey, extras)

        assertEquals(dataKey, navigationData.dataKey)
        assertEquals(extras, navigationData.extras)
    }

    @Test
    fun `test navigation data with empty extras`() {
        val dataKey = "test_key"
        val navigationData = NavigationData(dataKey)

        assertEquals(dataKey, navigationData.dataKey)
        assertTrue("Extras should be empty", navigationData.extras.isEmpty())
    }
}
