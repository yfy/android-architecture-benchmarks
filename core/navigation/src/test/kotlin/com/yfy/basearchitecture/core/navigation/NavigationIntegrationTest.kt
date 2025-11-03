import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.yfy.basearchitecture.core.navigation.NavigationManagerImpl
import com.yfy.basearchitecture.core.navigation.constants.Routes
import com.yfy.basearchitecture.core.navigation.helpers.NavigationDataStore
import com.yfy.basearchitecture.core.navigation.helpers.NavigationResult
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NavigationIntegrationTest {

    private lateinit var navigationManager: NavigationManagerImpl
    private lateinit var mockNavController: NavController

    @Before
    fun setUp() {
        navigationManager = NavigationManagerImpl()
        mockNavController = mockk(relaxed = true)

        every { mockNavController.navigate(any<String>(), any<NavOptions>()) } just Runs
        every { mockNavController.navigateUp() } returns true
        every { mockNavController.popBackStack(any<Int>(), any<Boolean>()) } returns true

        navigationManager.setNavController(mockNavController)
        NavigationDataStore.clearData()
    }

    @After
    fun tearDown() {
        navigationManager.clearNavController()
        NavigationDataStore.clearData()
        clearAllMocks()
    }

    @Test
    fun `test complete navigation flow with data`() {
        // Test data
        data class TestUserData(val id: String, val name: String)

        val userData = TestUserData("123", "John Doe")

        // Navigate with data
        val result = navigationManager.navigate(Routes.Profile.PROFILE, userData)

        // Verify navigation succeeded
        assertTrue("Navigation should succeed", result is NavigationResult.Success)

        // Verify NavController was called with data key
        verify {
            mockNavController.navigate(
                route = match { route -> route.contains("dataKey=") },
                navOptions = null
            )
        }
    }

    @Test
    fun `test deep link to route conversion flow`() {
        // Test various deep link conversions
        val testCases = listOf(
            "yfy://auth/login",
            "yfy://profile",
            "yfy://home/search",
        )

        testCases.forEach { deepLink ->
            clearAllMocks()
            every { mockNavController.navigate(any<String>(), any<NavOptions>()) } just Runs
            every { mockNavController.navigate(any<androidx.navigation.NavDeepLinkRequest>()) } just Runs

            val result = navigationManager.navigate(deepLink)

            assertTrue(
                "Navigation should succeed for $deepLink",
                result is NavigationResult.Success
            )
            verify { mockNavController.navigate(any<androidx.navigation.NavDeepLinkRequest>()) }
        }
    }

    @Test
    fun `test parameter extraction and route building`() {
        val emailLink = "yfy://auth/login?email=test@example.com"
        val result = navigationManager.navigate(emailLink)

        assertTrue("Navigation should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigate(any<androidx.navigation.NavDeepLinkRequest>()) }
    }

    @Test
    fun `test error handling throughout the flow`() {
        // Test navigation without NavController
        navigationManager.clearNavController()
        val result1 = navigationManager.navigate(Routes.Auth.LOGIN)
        assertTrue("Should fail without NavController", result1 is NavigationResult.Error)

        // Test navigation with invalid destination
        navigationManager.setNavController(mockNavController)
        val result2 = navigationManager.navigate("")
        assertTrue("Should fail with empty destination", result2 is NavigationResult.Error)

        // Test navigation with invalid deep link
        val result3 = navigationManager.navigate("invalid://example.com")
        assertTrue("Should fail with invalid deep link", result3 is NavigationResult.Error)
    }
}