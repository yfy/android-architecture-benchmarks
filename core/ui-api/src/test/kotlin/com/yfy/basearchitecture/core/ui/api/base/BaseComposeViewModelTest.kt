package com.yfy.basearchitecture.core.ui.api.base

import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BaseComposeViewModelTest {

    private lateinit var testViewModel: TestComposeViewModel
    private lateinit var mockUiHandler: UiHandler
    private lateinit var mockResourceProvider: ResourceProvider
    private lateinit var mockAnalytics: AnalyticsProvider
    private lateinit var mockNavigationManager: NavigationManager

    @Before
    fun setup() {
        mockUiHandler = mockk(relaxed = true)
        mockResourceProvider = mockk(relaxed = true)
        mockAnalytics = mockk(relaxed = true)
        mockNavigationManager = mockk(relaxed = true)
        
        testViewModel = TestComposeViewModel()
        testViewModel.uiHandler = mockUiHandler
        testViewModel.resourceProvider = mockResourceProvider
        testViewModel.analytics = mockAnalytics
        testViewModel.navigationManager = mockNavigationManager
    }

    @Test
    fun `should initialize with correct default states`() = runTest {
        // Then
        assertEquals(TestUiState("initial"), testViewModel.testState.first())
        assertFalse(testViewModel.isLoading.first())
        assertNull(testViewModel.dialogState.first())
        assertNull(testViewModel.bottomSheetState.first())
    }

    @Test
    fun `should set ui state correctly`() = runTest {
        // Given
        val newState = TestUiState("updated")

        // When
        testViewModel.setTestState(newState)

        // Then
        assertEquals(newState, testViewModel.testState.first())
    }

    @Test
    fun `should set loading state correctly`() = runTest {
        // When
        testViewModel.setTestLoading(true)

        // Then
        assertTrue(testViewModel.isLoading.first())

        // When
        testViewModel.setTestLoading(false)

        // Then
        assertFalse(testViewModel.isLoading.first())
    }

    @Test
    fun `should show and dismiss dialog correctly`() = runTest {
        // Given
        val dialog = DialogState.Info("Test Title", "Test Message")

        // When
        testViewModel.showTestDialog(dialog)

        // Then
        assertEquals(dialog, testViewModel.dialogState.first())

        // When
        testViewModel.dismissDialog()

        // Then
        assertNull(testViewModel.dialogState.first())
    }

    @Test
    fun `should show and dismiss bottom sheet correctly`() = runTest {
        // Given
        val bottomSheet = BottomSheetState.Custom({})

        // When
        testViewModel.showTestBottomSheet(bottomSheet)

        // Then
        assertEquals(bottomSheet, testViewModel.bottomSheetState.first())

        // When
        testViewModel.dismissBottomSheet()

        // Then
        assertNull(testViewModel.bottomSheetState.first())
    }

    @Test
    fun `should handle error correctly`() = runTest {
        // Given
        val error = BaseError.UnknownError("Test error")

        // When
        testViewModel.handleError(error)

        // Then
        verify { mockUiHandler.showError(error) }
    }

    @Test
    fun `should log screen correctly`() = runTest {
        // When
        testViewModel.logScreen("test_screen")

        // Then
        verify { mockAnalytics.logScreen("test_screen") }
    }

    @Test
    fun `should log event correctly`() = runTest {
        // Given
        val eventName = "test_event"
        val parameters = mapOf("key" to "value")

        // When
        testViewModel.logEvent(eventName, parameters)

        // Then
        verify { mockAnalytics.logEvent(eventName, parameters) }
    }

    @Test
    fun `should log event without parameters`() = runTest {
        // Given
        val eventName = "test_event"

        // When
        testViewModel.logEvent(eventName)

        // Then
        verify { mockAnalytics.logEvent(eventName, emptyMap()) }
    }

    @Test
    fun `should handle exception correctly`() = runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        testViewModel.exceptionHandler.handleException(Thread.currentThread(), exception)

        // Then
        verify { mockUiHandler.showError(any()) }
    }

    // Test implementation of BaseComposeViewModel
    private class TestComposeViewModel : BaseComposeViewModel() {
        private val _testState = MutableStateFlow(TestUiState("initial"))
        val testState: StateFlow<TestUiState> = _testState.asStateFlow()
        
        // Expose protected methods for testing
        fun setTestState(state: TestUiState) = _testState.value = state
        fun setTestLoading(loading: Boolean) = setLoading(loading)
        fun showTestDialog(dialog: DialogState) = showDialog(dialog)
        fun showTestBottomSheet(sheet: BottomSheetState) = showBottomSheet(sheet)
    }

    // Test UI state
    data class TestUiState(val value: String)
} 