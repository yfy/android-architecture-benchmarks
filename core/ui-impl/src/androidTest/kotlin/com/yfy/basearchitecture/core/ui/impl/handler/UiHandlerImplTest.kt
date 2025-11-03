package com.yfy.basearchitecture.core.ui.impl.handler

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.managers.LoaderType
import com.yfy.basearchitecture.core.ui.impl.backpress.BackPressManagerImpl
import com.yfy.basearchitecture.core.ui.impl.dialog.DialogManagerImpl
import com.yfy.basearchitecture.core.ui.impl.error.ErrorHandlerImpl
import com.yfy.basearchitecture.core.ui.impl.loader.LoaderManagerImpl
import com.yfy.basearchitecture.core.ui.impl.toast.ToastManagerImpl
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class UiHandlerImplTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var uiHandler: UiHandlerImpl
    private lateinit var mockErrorHandler: ErrorHandlerImpl
    private lateinit var mockLoaderManager: LoaderManagerImpl
    private lateinit var mockDialogManager: DialogManagerImpl
    private lateinit var mockBackPressManager: BackPressManagerImpl

    @Before
    fun setup() {
        mockErrorHandler = mockk(relaxed = true)
        mockLoaderManager = mockk(relaxed = true)
        mockDialogManager = mockk(relaxed = true)
        mockBackPressManager = mockk(relaxed = true)

        // Create UiHandlerImpl with mocked dependencies
        uiHandler = UiHandlerImpl(mockContext)
    }

    @Test
    fun should_delegateHandleError_when_errorProvided() {
        // Given
        val error = BaseError.NetworkError("Network error", 500, retryable = true)
        every { mockErrorHandler.handleError(error) } just Runs

        // When
        uiHandler.handleError(error)

        // Then
        verify { mockErrorHandler.handleError(error) }
    }

    @Test
    fun should_delegateShowError_when_errorProvided() {
        // Given
        val error = BaseError.ValidationError("Validation","Validation error")
        every { mockErrorHandler.showError(error) } just Runs

        // When
        uiHandler.showError(error)

        // Then
        verify { mockErrorHandler.showError(error) }
    }

    @Test
    fun should_delegateShouldShowError_when_errorProvided() {
        // Given
        val error = BaseError.NetworkError("Network error", 500, retryable = true)
        every { mockErrorHandler.shouldShowError(error) } returns true

        // When
        val result = uiHandler.shouldShowError(error)

        // Then
        verify { mockErrorHandler.shouldShowError(error) }
        assertTrue(result)
    }

    @Test
    fun should_showErrorWithTitleAndMessage_when_provided() {
        // Given
        val title = "Error Title"
        val message = "Error Message"
        every { mockErrorHandler.showError(any()) } just Runs

        // When
        uiHandler.showError(title, message)

        // Then
        verify { mockErrorHandler.showError(any()) }
    }

    @Test
    fun should_showErrorDialog_when_parametersProvided() {
        // Given
        val title = "Error Title"
        val message = "Error Message"
        val positiveText = "OK"
        val negativeText = "Cancel"
        val onPositiveClick: (() -> Unit)? = { }
        val onNegativeClick: (() -> Unit)? = { }
        
        every { mockDialogManager.showDialog(any()) } just Runs

        // When
        uiHandler.showErrorDialog(title, message, positiveText, negativeText, onPositiveClick, onNegativeClick)

        // Then
        verify { mockDialogManager.showDialog(any()) }
    }

    @Test
    fun should_showRetryDialog_when_parametersProvided() {
        // Given
        val message = "Retry message"
        val onRetry: () -> Unit = { }
        val onCancel: (() -> Unit)? = { }
        
        every { mockDialogManager.showDialog(any()) } just Runs

        // When
        uiHandler.showRetryDialog(message, onRetry, onCancel)

        // Then
        verify { mockDialogManager.showDialog(any()) }
    }

    @Test
    fun should_delegateShowLoader_when_messageProvided() {
        // Given
        val message = "Loading..."
        every { mockLoaderManager.showLoader(message) } just Runs

        // When
        uiHandler.showLoader(message)

        // Then
        verify { mockLoaderManager.showLoader(message) }
    }

    @Test
    fun should_delegateHideLoader_when_called() {
        // Given
        every { mockLoaderManager.hideLoader() } just Runs

        // When
        uiHandler.hideLoader()

        // Then
        verify { mockLoaderManager.hideLoader() }
    }

    @Test
    fun should_delegateShowLoaderWithType_when_typeAndMessageProvided() {
        // Given
        val type = LoaderType.CIRCULAR
        val message = "Loading..."
        every { mockLoaderManager.showLoader(type, message) } just Runs

        // When
        uiHandler.showLoader(type, message)

        // Then
        verify { mockLoaderManager.showLoader(type, message) }
    }

    @Test
    fun should_delegateIsLoaderShowing_when_called() {
        // Given
        every { mockLoaderManager.isLoaderShowing() } returns true

        // When
        val result = uiHandler.isLoaderShowing()

        // Then
        verify { mockLoaderManager.isLoaderShowing() }
        assertTrue(result)
    }

    @Test
    fun should_delegateGetCurrentLoaderMessage_when_called() {
        // Given
        val message = "Current loader message"
        every { mockLoaderManager.getCurrentLoaderMessage() } returns message

        // When
        val result = uiHandler.getCurrentLoaderMessage()

        // Then
        verify { mockLoaderManager.getCurrentLoaderMessage() }
        assertEquals(message, result)
    }

    @Test
    fun should_delegateGetCurrentLoaderType_when_called() {
        // Given
        val type = LoaderType.CIRCULAR
        every { mockLoaderManager.getCurrentLoaderType() } returns type

        // When
        val result = uiHandler.getCurrentLoaderType()

        // Then
        verify { mockLoaderManager.getCurrentLoaderType() }
        assertEquals(type, result)
    }

    @Test
    fun should_delegateShowAlertDialog_when_parametersProvided() {
        // Given
        val title = "Alert Title"
        val message = "Alert Message"
        val positiveText = "OK"
        val onPositiveClick: (() -> Unit)? = { }
        
        every { mockDialogManager.showAlert(title, message, positiveText, onPositiveClick) } just Runs

        // When
        uiHandler.showAlertDialog(title, message, positiveText, onPositiveClick)

        // Then
        verify { mockDialogManager.showAlert(title, message, positiveText, onPositiveClick) }
    }

    @Test
    fun should_delegateShowConfirmationDialog_when_parametersProvided() {
        // Given
        val title = "Confirm Title"
        val message = "Confirm Message"
        val positiveText = "Yes"
        val negativeText = "No"
        val onPositiveClick: (() -> Unit)? = { }
        val onNegativeClick: (() -> Unit)? = { }
        
        every { mockDialogManager.showConfirmation(title, message, positiveText, negativeText, onPositiveClick, onNegativeClick) } just Runs

        // When
        uiHandler.showConfirmationDialog(title, message, positiveText, negativeText, onPositiveClick, onNegativeClick)

        // Then
        verify { mockDialogManager.showConfirmation(title, message, positiveText, negativeText, onPositiveClick, onNegativeClick) }
    }

    @Test
    fun should_delegateHideAllDialogs_when_called() {
        // Given
        every { mockDialogManager.hideAllDialogs() } just Runs

        // When
        uiHandler.hideAllDialogs()

        // Then
        verify { mockDialogManager.hideAllDialogs() }
    }

    @Test
    fun should_delegateIsAnyDialogShowing_when_called() {
        // Given
        every { mockDialogManager.isAnyDialogShowing() } returns true

        // When
        val result = uiHandler.isAnyDialogShowing()

        // Then
        verify { mockDialogManager.isAnyDialogShowing() }
        assertTrue(result)
    }

    @Test
    fun should_handleNullCallbacks_when_provided() {
        // Given
        val error = BaseError.NetworkError("Network error", 500, retryable = true)
        every { mockErrorHandler.showError(error) } just Runs

        // When
        uiHandler.showErrorDialogWithRetry(
            error = error,
            onDismiss = { },
            onRetry = null,
            onConfirm = null,
            confirmText = "OK",
            retryText = "Retry",
            title = "Error"
        )

        // Then - Should not throw exception
        verify { mockDialogManager.showDialog(any()) }
    }

    @Test
    fun should_createInstance_when_contextProvided() {
        // Given
        val context = mockk<Context>()

        // When
        val uiHandler = UiHandlerImpl(context)

        // Then
        assertNotNull(uiHandler)
    }
} 