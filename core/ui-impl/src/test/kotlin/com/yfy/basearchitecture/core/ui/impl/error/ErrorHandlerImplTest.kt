package com.yfy.basearchitecture.core.ui.impl.error

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.SnackbarDuration
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
class ErrorHandlerImplTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var errorHandler: ErrorHandlerImpl

    @Before
    fun setup() {
        errorHandler = ErrorHandlerImpl(mockContext)
    }

    @Test
    fun should_handleError_when_errorProvided() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        var callbackInvoked = false
        errorHandler.setErrorSnackbarCallback { _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.handleError(error)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_showError_when_errorProvided() {
        // Given
        val error = BaseError.ValidationError("Validation", "Validation error")
        var callbackInvoked = false
        errorHandler.setErrorSnackbarCallback { _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showError(error)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_returnTrue_when_validationErrorProvided() {
        // Given
        val error = BaseError.ValidationError("Validation","Validation error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnTrue_when_networkErrorProvided() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnTrue_when_authenticationErrorProvided() {
        // Given
        val error = BaseError.AuthenticationError("Auth error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnTrue_when_permissionErrorProvided() {
        // Given
        val error = BaseError.PermissionError("Permission error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnTrue_when_serverErrorProvided() {
        // Given
        val error = BaseError.ServerError("Server error", 500)

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnTrue_when_timeoutErrorProvided() {
        // Given
        val error = BaseError.TimeoutError("Timeout error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnTrue_when_noInternetErrorProvided() {
        // Given
        val error = BaseError.NoInternetError("No internet error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertTrue(result)
    }

    @Test
    fun should_returnFalse_when_databaseErrorProvided() {
        // Given
        val error = BaseError.DatabaseError("Database error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertFalse(result)
    }

    @Test
    fun should_returnFalse_when_unknownErrorProvided() {
        // Given
        val error = BaseError.UnknownError("Unknown error")

        // When
        val result = errorHandler.shouldShowError(error)

        // Then
        assertFalse(result)
    }

    @Test
    fun should_showErrorWithTitleAndMessage_when_provided() {
        // Given
        val title = "Error Title"
        val message = "Error Message"
        var callbackInvoked = false
        errorHandler.setErrorSnackbarCallback { _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showError(title, message)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_showErrorSnackbar_when_messageAndDurationProvided() {
        // Given
        val message = "Error message"
        val duration = SnackbarDuration.LONG
        var callbackInvoked = false
        errorHandler.setErrorSnackbarCallback { _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showErrorSnackbar(message, duration)

        // Then
        assertTrue(callbackInvoked)
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
        
        var callbackInvoked = false
        errorHandler.setErrorDialogCallback { _, _, _, _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showErrorDialog(title, message, positiveText, negativeText, onPositiveClick, onNegativeClick)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_showRetryDialog_when_parametersProvided() {
        // Given
        val message = "Retry message"
        val onRetry: () -> Unit = { }
        val onCancel: (() -> Unit)? = { }
        
        var callbackInvoked = false
        errorHandler.setErrorDialogCallback { _, _, _, _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showRetryDialog(message, onRetry, onCancel)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_showErrorDialogWithRetryableError_when_retryableErrorProvided() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        val onDismiss: () -> Unit = { }
        val onRetry: (() -> Unit)? = { }
        val onConfirm: (() -> Unit)? = { }
        
        var callbackInvoked = false
        errorHandler.setErrorDialogCallback { _, _, _, _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showErrorDialog(error, onDismiss, onRetry, onConfirm, "OK", "Retry", "Error")

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_showErrorDialogWithNonRetryableError_when_nonRetryableErrorProvided() {
        // Given
        val error = BaseError.UnknownError("Unknown error")
        val onDismiss: () -> Unit = { }
        val onConfirm: (() -> Unit)? = { }
        
        var callbackInvoked = false
        errorHandler.setErrorDialogCallback { _, _, _, _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.showErrorDialog(error, onDismiss, null, onConfirm, "OK", "Retry", "Error")

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_handleNullCallbacks_when_provided() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        val onDismiss: () -> Unit = { }

        // When & Then - Should not throw exception
        errorHandler.showErrorDialog(error, onDismiss, null, null, "OK", "Retry", "Error")
    }

    @Test
    fun should_handleEmptyTitle_when_provided() {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        val onDismiss: () -> Unit = { }
        
        var callbackInvoked = false
        errorHandler.setErrorDialogCallback { _, title, _, _, _, _ -> 
            callbackInvoked = true
            assertEquals("Error", title)
        }

        // When
        errorHandler.showErrorDialog(error, onDismiss, null, null, "OK", "Retry", "")

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_createInstance_when_contextProvided() {
        // Given
        val context = mockk<Context>()

        // When
        val errorHandler = ErrorHandlerImpl(context)

        // Then
        assertNotNull(errorHandler)
    }

    @Test
    fun should_setErrorSnackbarCallback_when_callbackProvided() {
        // Given
        var callbackInvoked = false
        val callback: (BaseError?, String, SnackbarDuration) -> Unit = { _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.setErrorSnackbarCallback(callback)
        errorHandler.showErrorSnackbar("Test", SnackbarDuration.SHORT)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_setErrorDialogCallback_when_callbackProvided() {
        // Given
        var callbackInvoked = false
        val callback: (BaseError, String, String, String?, (() -> Unit)?, (() -> Unit)?) -> Unit = 
            { _, _, _, _, _, _ -> callbackInvoked = true }

        // When
        errorHandler.setErrorDialogCallback(callback)
        errorHandler.showErrorDialog("Title", "Message", "OK", "Cancel", null, null)

        // Then
        assertTrue(callbackInvoked)
    }

    @Test
    fun should_handleAllErrorTypes_when_provided() {
        // Given
        val errors = listOf(
            BaseError.ValidationError("Validation","Validation error"),
            BaseError.NetworkError("Network error", retryable = true),
            BaseError.AuthenticationError("Auth error"),
            BaseError.PermissionError("Permission error"),
            BaseError.ServerError("Server error",500),
            BaseError.TimeoutError("Timeout error"),
            BaseError.NoInternetError("No internet error"),
            BaseError.DatabaseError("Database error"),
            BaseError.UnknownError("Unknown error")
        )

        // When & Then
        errors.forEach { error ->
            val shouldShow = errorHandler.shouldShowError(error)
            when (error) {
                is BaseError.DatabaseError, is BaseError.UnknownError -> assertFalse(shouldShow)
                else -> assertTrue(shouldShow)
            }
        }
    }
} 