import com.yfy.basearchitecture.core.navigation.deeplink.LinkDetector
import com.yfy.basearchitecture.core.navigation.deeplink.LinkType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LinkDetectorTest {

    @Test
    fun `test deep link detection`() {
        val deepLink = "yfy://auth/login"
        assertEquals(LinkType.DEEP_LINK, LinkDetector.detectLinkType(deepLink))
        assertTrue(LinkDetector.isValidLink(deepLink))
    }

    @Test
    fun `test app link detection`() {
        val appLink = "https://example.com/profile/123"
        assertEquals(LinkType.APP_LINK, LinkDetector.detectLinkType(appLink))
        assertTrue(LinkDetector.isValidLink(appLink))
    }

    @Test
    fun `test route detection`() {
        val route = "profile/edit"
        assertEquals(LinkType.ROUTE, LinkDetector.detectLinkType(route))
        assertTrue(LinkDetector.isValidLink(route))
    }

    @Test
    fun `test invalid link detection`() {
        val invalidLinks = listOf(
            "invalid://example.com",
            "http://wrongdomain.com",
            "",
            "ftp://example.com"
        )

        invalidLinks.forEach { link ->
            assertEquals("Link should be invalid: $link", LinkType.INVALID, LinkDetector.detectLinkType(link))
            assertFalse("Link should not be valid: $link", LinkDetector.isValidLink(link))
        }
    }

    @Test
    fun `test app link to deep link conversion`() {
        val appLink = "https://example.com/auth/login"
        val expectedDeepLink = "yfy://auth/login"

        assertEquals(expectedDeepLink, LinkDetector.convertAppLinkToDeepLink(appLink))
    }

    @Test
    fun `test deep link to app link conversion`() {
        val deepLink = "yfy://profile/123"
        val expectedAppLink = "https://example.com/profile/123"

        assertEquals(expectedAppLink, LinkDetector.convertDeepLinkToAppLink(deepLink))
    }

    @Test
    fun `test conversion with non-matching links`() {
        val nonAppLink = "https://google.com/search"
        val nonDeepLink = "https://example.com/page"

        assertEquals(nonAppLink, LinkDetector.convertAppLinkToDeepLink(nonAppLink))
        assertEquals(nonDeepLink, LinkDetector.convertDeepLinkToAppLink(nonDeepLink))
    }
}
