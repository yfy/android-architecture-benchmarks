
import com.yfy.basearchitecture.core.navigation.constants.DeepLinks
import com.yfy.basearchitecture.core.navigation.constants.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class RoutesAndLinksTest {

    @Test
    fun `test deep links structure`() {
        assertEquals("yfy://auth/login", DeepLinks.Auth.LOGIN)
        assertEquals("yfy://auth/register", DeepLinks.Auth.REGISTER)
        assertEquals("yfy://auth/forgot-password", DeepLinks.Auth.FORGOT_PASSWORD)

        assertEquals("yfy://profile", DeepLinks.Profile.PROFILE)
        assertEquals("yfy://profile/edit", DeepLinks.Profile.PROFILE_EDIT)
        assertEquals("yfy://profile/settings", DeepLinks.Profile.PROFILE_SETTINGS)

        assertEquals("yfy://home", DeepLinks.Home.MAIN)
        assertEquals("yfy://home/search", DeepLinks.Home.SEARCH)
        assertEquals("yfy://home/favorites", DeepLinks.Home.FAVORITES)
    }

    @Test
    fun `test routes structure`() {
        assertEquals("login", Routes.Auth.LOGIN)
        assertEquals("register", Routes.Auth.REGISTER)
        assertEquals("forgot_password", Routes.Auth.FORGOT_PASSWORD)

        assertEquals("profile", Routes.Profile.PROFILE)
        assertEquals("profile_edit", Routes.Profile.PROFILE_EDIT)
        assertEquals("profile_settings", Routes.Profile.PROFILE_SETTINGS)

        assertEquals("home", Routes.Home.MAIN)
        assertEquals("search", Routes.Home.SEARCH)
        assertEquals("favorites", Routes.Home.FAVORITES)
    }

    @Test
    fun `test deep links with parameters`() {
        val email = "test@example.com"
        val expectedLoginLink = "yfy://auth/login?email=test@example.com"
        assertEquals(expectedLoginLink, DeepLinks.Auth.loginWithEmail(email))

        val userId = "123"
        val expectedProfileLink = "yfy://profile/123"
        assertEquals(expectedProfileLink, DeepLinks.Profile.profileWithId(userId))

        val query = "kotlin android"
        val expectedSearchLink = "yfy://home/search?q=kotlin android"
        assertEquals(expectedSearchLink, DeepLinks.Home.searchWithQuery(query))
    }

    @Test
    fun `test routes with parameters`() {
        val email = "test@example.com"
        val expectedLoginRoute = "login?email=test@example.com"
        assertEquals(expectedLoginRoute, Routes.Auth.loginWithArgs(email))

        val loginRouteWithoutEmail = Routes.Auth.loginWithArgs()
        assertEquals("login", loginRouteWithoutEmail)

        val userId = "123"
        val expectedProfileRoute = "profile/123"
        assertEquals(expectedProfileRoute, Routes.Profile.profileWithId(userId))

        val query = "kotlin android"
        val expectedSearchRoute = "search?q=kotlin android"
        assertEquals(expectedSearchRoute, Routes.Home.searchWithQuery(query))
    }

    @Test
    fun `test parameter encoding`() {
        val specialChars = "test@example.com with spaces & symbols"

        val deepLink = DeepLinks.Auth.loginWithEmail(specialChars)
        assertTrue("Deep link should contain encoded email", deepLink.contains(specialChars))

        val route = Routes.Auth.loginWithArgs(specialChars)
        assertTrue("Route should contain encoded email", route.contains(specialChars))
    }
}
