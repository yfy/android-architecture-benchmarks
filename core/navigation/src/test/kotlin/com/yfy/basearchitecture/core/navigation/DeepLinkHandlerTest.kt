import com.yfy.basearchitecture.core.navigation.deeplink.DeepLinkHandler
import com.yfy.basearchitecture.core.navigation.deeplink.NavigationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DeepLinkHandlerTest {

    @Test
    fun `normalizeToDeepLink handles deep link and app link`() {
        val deep = "yfy://auth/login"
        val app = "https://example.com/auth/login"

        assertEquals(deep, DeepLinkHandler.normalizeToDeepLink(deep))
        assertEquals(deep, DeepLinkHandler.normalizeToDeepLink(app))
    }

    @Test
    fun `buildDeepLinkRequest builds from deep and app links`() {
        val deep = "yfy://profile/123"
        val app = "https://example.com/profile/123"

        assertTrue(DeepLinkHandler.buildDeepLinkRequest(deep) != null)
        assertTrue(DeepLinkHandler.buildDeepLinkRequest(app) != null)
    }

    @Test
    fun `normalizeToDeepLink returns null for invalid links`() {
        val invalidLinks = listOf(
            "",
            "https://wrongdomain.com/auth/login",
            "invalid://auth/login"
        )

        invalidLinks.forEach { link ->
            assertNull("Invalid link should return null: $link", DeepLinkHandler.normalizeToDeepLink(link))
        }
    }

    @Test
    fun `app link converts to deep link prefix`() {
        val appLink = "https://example.com/auth/login"
        val deep = DeepLinkHandler.normalizeToDeepLink(appLink)
        assertTrue(deep!!.startsWith(NavigationConfig.DEEP_LINK_PREFIX))
    }

    @Test
    fun `test invalid deep link`() {
        val invalidLinks = listOf(
            "",
            "https://wrongdomain.com/auth/login",
            "invalid://auth/login"
        )

        invalidLinks.forEach { link ->
            assertNull("Invalid link should return null: $link", DeepLinkHandler.buildDeepLinkRequest(link))
        }
    }

    @Test
    fun `test parameter extraction`() {
        val linkWithParams = "yfy://auth/login?email=test@example.com&token=abc123"
        val params = DeepLinkHandler.extractParameters(linkWithParams)

        assertEquals("test@example.com", params["email"])
        assertEquals("abc123", params["token"])
    }

    @Test
    fun `test parameter extraction with path id`() {
        val linkWithId = "yfy://profile/123"
        val params = DeepLinkHandler.extractParameters(linkWithId)

        assertEquals("123", params["id"])
    }

    @Test
    fun `test parameter extraction with special characters`() {
        val linkWithSpecialChars = "yfy://auth/login?email=user%40example.com&name=John%20Doe"
        val params = DeepLinkHandler.extractParameters(linkWithSpecialChars)

        assertEquals("user@example.com", params["email"])
        assertEquals("John Doe", params["name"])
    }

    @Test
    fun `test parameter extraction with empty parameters`() {
        val linkWithoutParams = "yfy://auth/login"
        val params = DeepLinkHandler.extractParameters(linkWithoutParams)

        assertTrue("Parameters should be empty", params.isEmpty())
    }

    @Test
    fun `test parameter extraction with malformed link`() {
        val malformedLink = "not-a-valid-uri"
        val params = DeepLinkHandler.extractParameters(malformedLink)

        assertTrue("Parameters should be empty for malformed link", params.isEmpty())
    }
}
