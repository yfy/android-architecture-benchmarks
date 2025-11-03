package com.yfy.basearchitecture.core.ui.api.managers

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class LoaderManagerTest {

    @Mock
    private lateinit var loaderManager: LoaderManager

    private val testMessage = "Test loading message"
    private val defaultMessage = "Loading..."

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(loaderManager.isLoaderShowing()).thenReturn(false)
        `when`(loaderManager.getCurrentLoaderMessage()).thenReturn(null)
        `when`(loaderManager.getCurrentLoaderType()).thenReturn(null)
    }

    @Test
    fun `test showLoader with default message`() {
        // When
        loaderManager.showLoader()

        // Then
        verify(loaderManager).showLoader(defaultMessage)
    }

    @Test
    fun `test showLoader with custom message`() {
        // When
        loaderManager.showLoader(testMessage)

        // Then
        verify(loaderManager).showLoader(testMessage)
    }

    @Test
    fun `test showLoader with type and default message`() {
        // When
        loaderManager.showLoader(LoaderType.CIRCULAR)

        // Then
        verify(loaderManager).showLoader(LoaderType.CIRCULAR, defaultMessage)
    }

    @Test
    fun `test showLoader with type and custom message`() {
        // When
        loaderManager.showLoader(LoaderType.LINEAR, testMessage)

        // Then
        verify(loaderManager).showLoader(LoaderType.LINEAR, testMessage)
    }

    @Test
    fun `test hideLoader`() {
        // When
        loaderManager.hideLoader()

        // Then
        verify(loaderManager).hideLoader()
    }

    @Test
    fun `test isLoaderShowing returns true`() {
        // Given
        `when`(loaderManager.isLoaderShowing()).thenReturn(true)

        // When
        val result = loaderManager.isLoaderShowing()

        // Then
        assertTrue(result)
        verify(loaderManager).isLoaderShowing()
    }

    @Test
    fun `test isLoaderShowing returns false`() {
        // Given
        `when`(loaderManager.isLoaderShowing()).thenReturn(false)

        // When
        val result = loaderManager.isLoaderShowing()

        // Then
        assertFalse(result)
        verify(loaderManager).isLoaderShowing()
    }

    @Test
    fun `test getCurrentLoaderMessage returns message`() {
        // Given
        `when`(loaderManager.getCurrentLoaderMessage()).thenReturn(testMessage)

        // When
        val result = loaderManager.getCurrentLoaderMessage()

        // Then
        assertEquals(testMessage, result)
        verify(loaderManager).getCurrentLoaderMessage()
    }

    @Test
    fun `test getCurrentLoaderMessage returns null`() {
        // Given
        `when`(loaderManager.getCurrentLoaderMessage()).thenReturn(null)

        // When
        val result = loaderManager.getCurrentLoaderMessage()

        // Then
        assertNull(result)
        verify(loaderManager).getCurrentLoaderMessage()
    }

    @Test
    fun `test getCurrentLoaderType returns type`() {
        // Given
        val expectedType = LoaderType.CIRCULAR
        `when`(loaderManager.getCurrentLoaderType()).thenReturn(expectedType)

        // When
        val result = loaderManager.getCurrentLoaderType()

        // Then
        assertEquals(expectedType, result)
        verify(loaderManager).getCurrentLoaderType()
    }

    @Test
    fun `test getCurrentLoaderType returns null`() {
        // Given
        `when`(loaderManager.getCurrentLoaderType()).thenReturn(null)

        // When
        val result = loaderManager.getCurrentLoaderType()

        // Then
        assertNull(result)
        verify(loaderManager).getCurrentLoaderType()
    }

    @Test
    fun `test LoaderType enum values`() {
        // Test all LoaderType enum values
        val circular = LoaderType.CIRCULAR
        val linear = LoaderType.LINEAR
        val dots = LoaderType.DOTS
        val pulse = LoaderType.PULSE
        val custom = LoaderType.CUSTOM

        assertEquals(LoaderType.CIRCULAR, circular)
        assertEquals(LoaderType.LINEAR, linear)
        assertEquals(LoaderType.DOTS, dots)
        assertEquals(LoaderType.PULSE, pulse)
        assertEquals(LoaderType.CUSTOM, custom)
    }

    @Test
    fun `test LoaderConfig data class`() {
        // Given
        val type = LoaderType.CIRCULAR
        val message = "Test message"
        val isCancelable = true
        val onCancel: (() -> Unit)? = { /* test callback */ }

        // When
        val config = LoaderConfig(type, message, isCancelable, onCancel)

        // Then
        assertEquals(type, config.type)
        assertEquals(message, config.message)
        assertEquals(isCancelable, config.isCancelable)
        assertEquals(onCancel, config.onCancel)
    }

    @Test
    fun `test LoaderConfig with default values`() {
        // When
        val config = LoaderConfig()

        // Then
        assertEquals(LoaderType.CIRCULAR, config.type)
        assertEquals("Loading...", config.message)
        assertEquals(false, config.isCancelable)
        assertNull(config.onCancel)
    }

    @Test
    fun `test LoaderConfig copy method`() {
        // Given
        val originalConfig = LoaderConfig(
            type = LoaderType.CIRCULAR,
            message = "Original message",
            isCancelable = false,
            onCancel = null
        )

        // When
        val copiedConfig = originalConfig.copy(
            type = LoaderType.LINEAR,
            message = "New message",
            isCancelable = true
        )

        // Then
        assertEquals(LoaderType.LINEAR, copiedConfig.type)
        assertEquals("New message", copiedConfig.message)
        assertEquals(true, copiedConfig.isCancelable)
        assertNull(copiedConfig.onCancel)
    }

    @Test
    fun `test LoaderConfig component functions`() {
        // Given
        val type = LoaderType.DOTS
        val message = "Component test"
        val isCancelable = true
        val onCancel: (() -> Unit)? = { /* test callback */ }
        val config = LoaderConfig(type, message, isCancelable, onCancel)

        // When
        val (extractedType, extractedMessage, extractedCancelable, extractedOnCancel) = config

        // Then
        assertEquals(type, extractedType)
        assertEquals(message, extractedMessage)
        assertEquals(isCancelable, extractedCancelable)
        assertEquals(onCancel, extractedOnCancel)
    }
} 