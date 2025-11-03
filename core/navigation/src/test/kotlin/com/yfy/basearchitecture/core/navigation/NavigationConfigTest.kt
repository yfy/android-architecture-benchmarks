import com.yfy.basearchitecture.core.navigation.deeplink.NavigationConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NavigationConfigTest {

    @Test
    fun `test navigation config constants`() {
        assertEquals("yfy", NavigationConfig.DEEP_LINK_SCHEME)
        assertEquals("example.com", NavigationConfig.APP_LINK_DOMAIN)
        assertEquals("yfy://", NavigationConfig.DEEP_LINK_PREFIX)
        assertEquals("https://example.com/", NavigationConfig.APP_LINK_PREFIX)
    }
}