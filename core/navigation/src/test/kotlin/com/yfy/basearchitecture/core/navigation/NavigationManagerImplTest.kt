
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.yfy.basearchitecture.core.navigation.NavigationManagerImpl
import com.yfy.basearchitecture.core.navigation.constants.Routes
import com.yfy.basearchitecture.core.navigation.helpers.NavigationResult
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NavigationManagerImplTest {

    private lateinit var navigationManager: NavigationManagerImpl
    private lateinit var mockNavController: NavController

    @Before
    fun setUp() {
        navigationManager = NavigationManagerImpl()
        mockNavController = mockk(relaxed = true)

        // Mock NavController behavior
        every { mockNavController.navigate(any<String>(), any<NavOptions>()) } just Runs
        every { mockNavController.navigateUp() } returns true
        every { mockNavController.popBackStack(any<Int>(), any<Boolean>()) } returns true

        navigationManager.setNavController(mockNavController)
    }

    @After
    fun tearDown() {
        navigationManager.clearNavController()
        clearAllMocks()
    }

    @Test
    fun `test route navigation success`() {
        val destination = Routes.Auth.LOGIN
        val result = navigationManager.navigate(destination)

        assertTrue("Navigation should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigate(destination, null) }
    }

    @Test
    fun `test route navigation with data`() {
        val destination = Routes.Profile.PROFILE
        val testData = "test_data"

        val result = navigationManager.navigate(destination, testData)

        assertTrue("Navigation should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigate(route = match { it.contains("dataKey=") },navOptions =  null) }
    }

    @Test
    fun `test deep link navigation`() {
        val deepLink = "yfy://auth/login"
        val result = navigationManager.navigate(deepLink, null)

        assertTrue("Deep link navigation should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigate(any<androidx.navigation.NavDeepLinkRequest>()) }
    }

    @Test
    fun `test navigation without nav controller`() {
        navigationManager.clearNavController()

        val result = navigationManager.navigate(Routes.Auth.LOGIN)

        assertTrue("Navigation should fail", result is NavigationResult.Error)
        assertEquals("NavController is not set", (result as NavigationResult.Error).message)
    }

    @Test
    fun `test navigation with empty destination`() {
        val result = navigationManager.navigate("")

        assertTrue("Navigation should fail", result is NavigationResult.Error)
        assertEquals("Destination cannot be empty", (result as NavigationResult.Error).message)
    }

    @Test
    fun `test navigation with invalid link`() {
        val invalidLink = "invalid://example.com"
        val result = navigationManager.navigate(invalidLink)

        assertTrue("Navigation should fail", result is NavigationResult.Error)
        assertTrue("Error message should contain 'Invalid link format'",
            (result as NavigationResult.Error).message.contains("Invalid link format"))
    }

    @Test
    fun `test navigate up success`() {
        val result = navigationManager.navigateUp()

        assertTrue("Navigate up should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigateUp() }
    }

    @Test
    fun `test navigate up failure`() {
        every { mockNavController.navigateUp() } returns false

        val result = navigationManager.navigateUp()

        assertTrue("Navigate up should fail", result is NavigationResult.Error)
        assertEquals("Cannot navigate up", (result as NavigationResult.Error).message)
    }

    @Test
    fun `test navigate to root`() {
        val mockGraph = mockk<androidx.navigation.NavGraph>()
        every { mockGraph.startDestinationId } returns 1
        every { mockNavController.graph } returns mockGraph

        val result = navigationManager.navigateToRoot()

        assertTrue("Navigate to root should succeed", result is NavigationResult.Success)
        verify { mockNavController.popBackStack(1, false) }
    }

    @Test
    fun `test clear back stack`() {
        val mockGraph = mockk<androidx.navigation.NavGraph>()
        every { mockGraph.startDestinationId } returns 1
        every { mockNavController.graph } returns mockGraph

        val result = navigationManager.clearBackStack()

        assertTrue("Clear back stack should succeed", result is NavigationResult.Success)
        verify { mockNavController.popBackStack(1, true) }
    }

    @Test
    fun `test handle deep link`() {
        val deepLink = "yfy://auth/login"
        val result = navigationManager.handleDeepLink(deepLink)

        assertTrue("Deep link handling should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigate(any<androidx.navigation.NavDeepLinkRequest>()) }
    }

    @Test
    fun `test handle invalid deep link`() {
        val invalidDeepLink = "abc://invalid/path"
        val result = navigationManager.handleDeepLink(invalidDeepLink)

        assertTrue("Invalid deep link handling should fail", result is NavigationResult.Error)
        assertTrue("Error message should contain 'Invalid deep link'",
            (result as NavigationResult.Error).message.contains("Invalid deep link"))
    }

    @Test
    fun `test navigation with nav options`() {
        val destination = Routes.Auth.LOGIN
        val navOptions = mockk<NavOptions>()

        val result = navigationManager.navigate(destination, navOptions = navOptions)

        assertTrue("Navigation with options should succeed", result is NavigationResult.Success)
        verify { mockNavController.navigate(destination, navOptions) }
    }

    @Test
    fun `test navigation exception handling`() {
        every { mockNavController.navigate(any<String>(), any<NavOptions>()) } throws RuntimeException("Test exception")

        val result = navigationManager.navigate(Routes.Auth.LOGIN)

        assertTrue("Navigation should fail with exception", result is NavigationResult.Error)
        assertTrue("Error message should contain exception message",
            (result as NavigationResult.Error).message.contains("Test exception"))
    }
}
