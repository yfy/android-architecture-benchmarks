package com.yfy.basearchitecture.core.ui.api.base

import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BaseViewModelTest {

    private lateinit var testViewModel: TestBaseViewModel
    private lateinit var mockUiHandler: UiHandler
    private lateinit var mockResourceProvider: ResourceProvider
    private lateinit var mockAnalytics: AnalyticsProvider

    @Before
    fun setup() {
        mockUiHandler = mockk(relaxed = true)
        mockResourceProvider = mockk(relaxed = true)
        mockAnalytics = mockk(relaxed = true)
        
        testViewModel = TestBaseViewModel()
        testViewModel.uiHandler = mockUiHandler
        testViewModel.resourceProvider = mockResourceProvider
        testViewModel.analytics = mockAnalytics
    }

    @Test
    fun `should initialize with correct default states`() = runTest {
        // Then
        assertFalse(testViewModel.isLoading.first())
        assertNull(testViewModel.error.first())
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
    fun `should set error state correctly`() = runTest {
        // Given
        val error = BaseError.NetworkError("Network error")
        
        // When
        testViewModel.setTestError(error)
        
        // Then
        assertEquals(error, testViewModel.error.first())
        
        // When
        testViewModel.clearError()
        
        // Then
        assertNull(testViewModel.error.first())
    }

    @Test
    fun `should handle error with toast when showToast is true`() = runTest {
        // Given
        val error = BaseError.NetworkError("Network error")
        
        // When
        testViewModel.handleError(error, showToast = true)
        
        // Then
        verify { mockUiHandler.showError(error) }
        assertEquals(error, testViewModel.error.first())
    }

    @Test
    fun `should handle error with log only when logOnly is true`() = runTest {
        // Given
        val error = BaseError.NetworkError("Network error")
        
        // When
        testViewModel.handleError(error, showToast = false, logOnly = true)
        
        // Then
        verify { mockUiHandler.logError(error) }
        assertEquals(error, testViewModel.error.first())
    }

    @Test
    fun `should log screen on activity created`() = runTest {
        // When
        testViewModel.onActivityCreated()
        
        // Then
        verify { mockAnalytics.logScreen("TestBaseViewModel") }
    }

    @Test
    fun `should log event with parameters`() = runTest {
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
    fun `should call lifecycle methods in correct order`() = runTest {
        // When
        testViewModel.onActivityCreated()
        testViewModel.onActivityResumed()
        testViewModel.onActivityPaused()
        testViewModel.onActivityDestroyed()
        
        // Then
        // All methods should execute without exception
        // This test verifies the lifecycle methods are callable
    }

    // Test implementation of BaseViewModel
    private class TestBaseViewModel : BaseViewModel() {
        // Expose protected methods for testing
        fun setTestLoading(loading: Boolean) = setLoading(loading)
        fun setTestError(error: BaseError?) = setError(error)
    }
} 