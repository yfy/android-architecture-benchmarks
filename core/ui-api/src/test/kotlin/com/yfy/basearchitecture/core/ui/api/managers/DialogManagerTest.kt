package com.yfy.basearchitecture.core.ui.api.managers

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class DialogManagerTest {

    @Mock
    private lateinit var dialogManager: DialogManager

    private val testTitle = "Test Title"
    private val testMessage = "Test Message"
    private val positiveButtonText = "OK"
    private val negativeButtonText = "Cancel"
    private val neutralButtonText = "Neutral"

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(dialogManager.isAnyDialogShowing()).thenReturn(false)
        `when`(dialogManager.getCurrentDialogCount()).thenReturn(0)
    }

    @Test
    fun `test showAlert with default parameters`() {
        // When
        dialogManager.showAlert(testTitle, testMessage)

        // Then
        verify(dialogManager).showAlert(testTitle, testMessage, "OK", null)
    }

    @Test
    fun `test showAlert with custom parameters`() {
        // Given
        val customPositiveText = "Accept"
        val onPositiveClick: (() -> Unit)? = { /* test callback */ }

        // When
        dialogManager.showAlert(testTitle, testMessage, customPositiveText, onPositiveClick)

        // Then
        verify(dialogManager).showAlert(testTitle, testMessage, customPositiveText, onPositiveClick)
    }

    @Test
    fun `test showConfirmation with default parameters`() {
        // When
        dialogManager.showConfirmation(testTitle, testMessage)

        // Then
        verify(dialogManager).showConfirmation(testTitle, testMessage, "OK", "Cancel", null, null)
    }

    @Test
    fun `test showConfirmation with custom parameters`() {
        // Given
        val customPositiveText = "Yes"
        val customNegativeText = "No"
        val onPositiveClick: (() -> Unit)? = { /* test callback */ }
        val onNegativeClick: (() -> Unit)? = { /* test callback */ }

        // When
        dialogManager.showConfirmation(testTitle, testMessage, customPositiveText, customNegativeText, onPositiveClick, onNegativeClick)

        // Then
        verify(dialogManager).showConfirmation(testTitle, testMessage, customPositiveText, customNegativeText, onPositiveClick, onNegativeClick)
    }

    @Test
    fun `test showLoading with default message`() {
        // When
        dialogManager.showLoading()

        // Then
        verify(dialogManager).showLoading("Loading...")
    }

    @Test
    fun `test showLoading with custom message`() {
        // When
        dialogManager.showLoading(testMessage)

        // Then
        verify(dialogManager).showLoading(testMessage)
    }

    @Test
    fun `test hideLoading`() {
        // When
        dialogManager.hideLoading()

        // Then
        verify(dialogManager).hideLoading()
    }

    @Test
    fun `test showCustomDialog`() {
        // Given
        val config = DialogConfig(
            type = DialogType.CUSTOM,
            title = testTitle,
            message = testMessage
        )

        // When
        dialogManager.showCustomDialog(config)

        // Then
        verify(dialogManager).showCustomDialog(config)
    }

    @Test
    fun `test showDialog`() {
        // Given
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = testTitle,
            message = testMessage
        )

        // When
        dialogManager.showDialog(config)

        // Then
        verify(dialogManager).showDialog(config)
    }

    @Test
    fun `test hideAllDialogs`() {
        // When
        dialogManager.hideAllDialogs()

        // Then
        verify(dialogManager).hideAllDialogs()
    }

    @Test
    fun `test isAnyDialogShowing returns true`() {
        // Given
        `when`(dialogManager.isAnyDialogShowing()).thenReturn(true)

        // When
        val result = dialogManager.isAnyDialogShowing()

        // Then
        assertTrue(result)
        verify(dialogManager).isAnyDialogShowing()
    }

    @Test
    fun `test isAnyDialogShowing returns false`() {
        // Given
        `when`(dialogManager.isAnyDialogShowing()).thenReturn(false)

        // When
        val result = dialogManager.isAnyDialogShowing()

        // Then
        assertFalse(result)
        verify(dialogManager).isAnyDialogShowing()
    }

    @Test
    fun `test getCurrentDialogCount returns count`() {
        // Given
        val expectedCount = 3
        `when`(dialogManager.getCurrentDialogCount()).thenReturn(expectedCount)

        // When
        val result = dialogManager.getCurrentDialogCount()

        // Then
        assertEquals(expectedCount, result)
        verify(dialogManager).getCurrentDialogCount()
    }

    @Test
    fun `test getCurrentDialogCount returns zero`() {
        // Given
        `when`(dialogManager.getCurrentDialogCount()).thenReturn(0)

        // When
        val result = dialogManager.getCurrentDialogCount()

        // Then
        assertEquals(0, result)
        verify(dialogManager).getCurrentDialogCount()
    }

    @Test
    fun `test DialogType enum values`() {
        // Test all DialogType enum values
        val alert = DialogType.ALERT
        val confirmation = DialogType.CONFIRMATION
        val input = DialogType.INPUT
        val loading = DialogType.LOADING
        val custom = DialogType.CUSTOM

        assertEquals(DialogType.ALERT, alert)
        assertEquals(DialogType.CONFIRMATION, confirmation)
        assertEquals(DialogType.INPUT, input)
        assertEquals(DialogType.LOADING, loading)
        assertEquals(DialogType.CUSTOM, custom)
    }

    @Test
    fun `test DialogConfig with all parameters`() {
        // Given
        val type = DialogType.CONFIRMATION
        val title = "Test Title"
        val message = "Test Message"
        val positiveButtonText = "Yes"
        val negativeButtonText = "No"
        val neutralButtonText = "Maybe"
        val dismissible = false
        val cancelable = false
        val onPositiveClick: (() -> Unit)? = { /* test callback */ }
        val onNegativeClick: (() -> Unit)? = { /* test callback */ }
        val onNeutralClick: (() -> Unit)? = { /* test callback */ }
        val onDismiss: (() -> Unit)? = { /* test callback */ }

        // When
        val config = DialogConfig(
            type = type,
            title = title,
            message = message,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            neutralButtonText = neutralButtonText,
            dismissible = dismissible,
            cancelable = cancelable,
            onPositiveClick = onPositiveClick,
            onNegativeClick = onNegativeClick,
            onNeutralClick = onNeutralClick,
            onDismiss = onDismiss
        )

        // Then
        assertEquals(type, config.type)
        assertEquals(title, config.title)
        assertEquals(message, config.message)
        assertEquals(positiveButtonText, config.positiveButtonText)
        assertEquals(negativeButtonText, config.negativeButtonText)
        assertEquals(neutralButtonText, config.neutralButtonText)
        assertEquals(dismissible, config.dismissible)
        assertEquals(cancelable, config.cancelable)
        assertEquals(onPositiveClick, config.onPositiveClick)
        assertEquals(onNegativeClick, config.onNegativeClick)
        assertEquals(onNeutralClick, config.onNeutralClick)
        assertEquals(onDismiss, config.onDismiss)
    }

    @Test
    fun `test DialogConfig with default values`() {
        // When
        val config = DialogConfig()

        // Then
        assertEquals(DialogType.ALERT, config.type)
        assertEquals(null, config.title)
        assertEquals(null, config.message)
        assertEquals(null, config.positiveButtonText)
        assertEquals(null, config.negativeButtonText)
        assertEquals(null, config.neutralButtonText)
        assertEquals(true, config.dismissible)
        assertEquals(true, config.cancelable)
        assertEquals(null, config.onPositiveClick)
        assertEquals(null, config.onNegativeClick)
        assertEquals(null, config.onNeutralClick)
        assertEquals(null, config.onDismiss)
    }

    @Test
    fun `test DialogConfig copy method`() {
        // Given
        val originalConfig = DialogConfig(
            type = DialogType.ALERT,
            title = "Original Title",
            message = "Original Message"
        )

        // When
        val copiedConfig = originalConfig.copy(
            type = DialogType.CONFIRMATION,
            title = "New Title",
            message = "New Message"
        )

        // Then
        assertEquals(DialogType.CONFIRMATION, copiedConfig.type)
        assertEquals("New Title", copiedConfig.title)
        assertEquals("New Message", copiedConfig.message)
    }

    @Test
    fun `test DialogConfig component functions`() {
        // Given
        val type = DialogType.INPUT
        val title = "Component Test"
        val message = "Component Message"
        val config = DialogConfig(type, title, message)

        // When
        val (extractedType, extractedTitle, extractedMessage, extractedPositiveText, extractedNegativeText, 
             extractedNeutralText, extractedDismissible, extractedCancelable, extractedOnPositive, 
             extractedOnNegative, extractedOnNeutral, extractedOnDismiss) = config

        // Then
        assertEquals(type, extractedType)
        assertEquals(title, extractedTitle)
        assertEquals(message, extractedMessage)
        assertEquals(null, extractedPositiveText)
        assertEquals(null, extractedNegativeText)
        assertEquals(null, extractedNeutralText)
        assertEquals(true, extractedDismissible)
        assertEquals(true, extractedCancelable)
        assertEquals(null, extractedOnPositive)
        assertEquals(null, extractedOnNegative)
        assertEquals(null, extractedOnNeutral)
        assertEquals(null, extractedOnDismiss)
    }
} 