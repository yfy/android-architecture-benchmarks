package com.yfy.basearchitecture.core.ui.impl.dialog

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.managers.DialogConfig
import com.yfy.basearchitecture.core.ui.api.managers.DialogType
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class DialogManagerImplTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var dialogManager: DialogManagerImpl

    @Before
    fun setup() {
        dialogManager = DialogManagerImpl(mockContext)
    }

    @Test
    fun should_showAlert_when_parametersProvided() {
        // Given
        val title = "Alert Title"
        val message = "Alert Message"
        val positiveText = "OK"
        val onPositiveClick: (() -> Unit)? = { }
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, _, _ -> callbackInvoked = true }

        // When
        dialogManager.showAlert(title, message, positiveText, onPositiveClick)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(1, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_showConfirmation_when_parametersProvided() {
        // Given
        val title = "Confirm Title"
        val message = "Confirm Message"
        val positiveText = "Yes"
        val negativeText = "No"
        val onPositiveClick: (() -> Unit)? = { }
        val onNegativeClick: (() -> Unit)? = { }
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, _, _ -> callbackInvoked = true }

        // When
        dialogManager.showConfirmation(title, message, positiveText, negativeText, onPositiveClick, onNegativeClick)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(1, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_showLoading_when_messageProvided() {
        // Given
        val message = "Loading..."
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, isLoading, loadingMessage -> 
            callbackInvoked = true
            assertTrue(isLoading)
            assertEquals(message, loadingMessage)
        }

        // When
        dialogManager.showLoading(message)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(1, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_hideLoading_when_called() {
        // Given
        dialogManager.showLoading("Loading...")
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, isLoading, loadingMessage -> 
            callbackInvoked = true
            assertFalse(isLoading)
            assertEquals("", loadingMessage)
        }

        // When
        dialogManager.hideLoading()

        // Then
        assertTrue(callbackInvoked)
        assertFalse(dialogManager.isAnyDialogShowing())
        assertEquals(0, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_showCustomDialog_when_configProvided() {
        // Given
        val config = DialogConfig(
            type = DialogType.CONFIRMATION,
            title = "Custom Title",
            message = "Custom Message",
            positiveButtonText = "OK",
            negativeButtonText = "Cancel"
        )
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, _, _ -> callbackInvoked = true }

        // When
        dialogManager.showCustomDialog(config)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(1, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_showDialog_when_configProvided() {
        // Given
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = "Dialog Title",
            message = "Dialog Message",
            positiveButtonText = "OK"
        )
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, _, _ -> callbackInvoked = true }

        // When
        dialogManager.showDialog(config)

        // Then
        assertTrue(callbackInvoked)
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(1, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_hideAllDialogs_when_called() {
        // Given
        dialogManager.showAlert("Title", "Message", "OK", null)
        dialogManager.showLoading("Loading...")
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, isLoading, loadingMessage -> 
            callbackInvoked = true
            assertFalse(isLoading)
            assertEquals("", loadingMessage)
        }

        // When
        dialogManager.hideAllDialogs()

        // Then
        assertTrue(callbackInvoked)
        assertFalse(dialogManager.isAnyDialogShowing())
        assertEquals(0, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_returnTrue_when_dialogShowing() {
        // Given
        dialogManager.showAlert("Title", "Message", "OK", null)

        // When
        val result = dialogManager.isAnyDialogShowing()

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnFalse_when_noDialogShowing() {
        // Given
        // No dialogs shown

        // When
        val result = dialogManager.isAnyDialogShowing()

        // Then
        assertFalse(result)
    }

    @Test
    fun should_returnCorrectCount_when_multipleDialogsShowing() {
        // Given
        dialogManager.showAlert("Title1", "Message1", "OK", null)
        dialogManager.showConfirmation("Title2", "Message2", "Yes", "No", null, null)
        dialogManager.showLoading("Loading...")

        // When
        val count = dialogManager.getCurrentDialogCount()

        // Then
        assertEquals(3, count)
    }

    @Test
    fun should_returnZeroCount_when_noDialogsShowing() {
        // Given
        // No dialogs shown

        // When
        val count = dialogManager.getCurrentDialogCount()

        // Then
        assertEquals(0, count)
    }

    @Test
    fun should_removeDialog_when_configProvided() {
        // Given
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = "Title",
            message = "Message",
            positiveButtonText = "OK"
        )
        dialogManager.showDialog(config)
        
        var callbackInvoked = false
        dialogManager.setDialogStateCallback { _, _, _ -> callbackInvoked = true }

        // When
        dialogManager.removeDialog(config)

        // Then
        assertTrue(callbackInvoked)
        assertFalse(dialogManager.isAnyDialogShowing())
        assertEquals(0, dialogManager.getCurrentDialogCount())
    }

    @Test
    fun should_getDialogState_when_called() {
        // Given
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = "Title",
            message = "Message",
            positiveButtonText = "OK"
        )
        dialogManager.showDialog(config)
        dialogManager.showLoading("Loading...")

        // When
        val state = dialogManager.getDialogState()

        // Then
        assertEquals(1, state.first.size)
        assertTrue(state.second) // isLoading
        assertEquals("Loading...", state.third) // loadingMessage
    }

    @Test
    fun should_handleNullCallbacks_when_provided() {
        // Given
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = "Title",
            message = "Message",
            positiveButtonText = "OK"
        )

        // When & Then - Should not throw exception
        dialogManager.showDialog(config)
        dialogManager.hideAllDialogs()
    }

    @Test
    fun should_createInstance_when_contextProvided() {
        // Given
        val context = mockk<Context>()

        // When
        val dialogManager = DialogManagerImpl(context)

        // Then
        assertNotNull(dialogManager)
    }

    @Test
    fun should_handleMultipleDialogs_when_provided() {
        // Given
        val config1 = DialogConfig(
            type = DialogType.ALERT,
            title = "Alert 1",
            message = "Message 1",
            positiveButtonText = "OK"
        )
        val config2 = DialogConfig(
            type = DialogType.CONFIRMATION,
            title = "Confirm 1",
            message = "Message 2",
            positiveButtonText = "Yes",
            negativeButtonText = "No"
        )

        // When
        dialogManager.showDialog(config1)
        dialogManager.showDialog(config2)

        // Then
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(2, dialogManager.getCurrentDialogCount())
        
        val state = dialogManager.getDialogState()
        assertEquals(2, state.first.size)
    }

    @Test
    fun should_handleLoadingWithDialogs_when_provided() {
        // Given
        val config = DialogConfig(
            type = DialogType.ALERT,
            title = "Title",
            message = "Message",
            positiveButtonText = "OK"
        )

        // When
        dialogManager.showDialog(config)
        dialogManager.showLoading("Loading...")

        // Then
        assertTrue(dialogManager.isAnyDialogShowing())
        assertEquals(2, dialogManager.getCurrentDialogCount())
        
        val state = dialogManager.getDialogState()
        assertEquals(1, state.first.size)
        assertTrue(state.second)
        assertEquals("Loading...", state.third)
    }
} 