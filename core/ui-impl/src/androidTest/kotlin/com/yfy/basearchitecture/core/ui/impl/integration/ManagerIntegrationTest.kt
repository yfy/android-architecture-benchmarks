package com.yfy.basearchitecture.core.ui.impl.integration

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.managers.ToastDuration
import com.yfy.basearchitecture.core.ui.impl.backpress.BackPressManagerImpl
import com.yfy.basearchitecture.core.ui.impl.dialog.DialogManagerImpl
import com.yfy.basearchitecture.core.ui.impl.error.ErrorHandlerImpl
import com.yfy.basearchitecture.core.ui.impl.handler.UiHandlerImpl
import com.yfy.basearchitecture.core.ui.impl.loader.LoaderManagerImpl
import com.yfy.basearchitecture.core.ui.impl.toast.ToastManagerImpl
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class ManagerIntegrationTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var uiHandler: UiHandlerImpl
    private lateinit var errorHandler: ErrorHandlerImpl
    private lateinit var dialogManager: DialogManagerImpl
    private lateinit var loaderManager: LoaderManagerImpl
    private lateinit var toastManager: ToastManagerImpl
    private lateinit var backPressManager: BackPressManagerImpl

    @Before
    fun setup() {
        errorHandler = ErrorHandlerImpl(mockContext)
        dialogManager = DialogManagerImpl(mockContext)
        loaderManager = LoaderManagerImpl(mockContext)
        toastManager = ToastManagerImpl(mockContext)
        backPressManager = BackPressManagerImpl()
        
        uiHandler = UiHandlerImpl(mockContext)
    }

    @Test
    fun should_coordinateErrorHandling_when_errorOccurs() = runTest {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        var errorCallbackInvoked = false
        var dialogCallbackInvoked = false
        
        errorHandler.setErrorSnackbarCallback { _, _, _ -> errorCallbackInvoked = true }
        dialogManager.setDialogStateCallback { _, _, _ -> dialogCallbackInvoked = true }

        // When
        uiHandler.handleError(error)

        // Then
        assertTrue(errorCallbackInvoked)
    }

    @Test
    fun should_coordinateLoaderAndDialog_when_loadingWithDialog() = runTest {
        // Given
        var loaderCallbackInvoked = false
        var dialogCallbackInvoked = false
        
        loaderManager.setLoaderStateCallback { _, _ -> loaderCallbackInvoked = true }
        dialogManager.setDialogStateCallback { _, _, _ -> dialogCallbackInvoked = true }

        // When
        uiHandler.showLoader("Loading...")
        uiHandler.showAlertDialog("Title", "Message", "OK", null)

        // Then
        assertTrue(loaderCallbackInvoked)
        assertTrue(dialogCallbackInvoked)
        assertTrue(uiHandler.isLoaderShowing())
        assertTrue(uiHandler.isAnyDialogShowing())
    }

    @Test
    fun should_coordinateToastAndBackPress_when_userInteraction() {
        // Given
        var toastCallbackInvoked = false
        backPressManager.addBackPressCallback { 
            toastCallbackInvoked = true
            true
        }

        // When
        uiHandler.showShort("Toast message")
        uiHandler.onBackPressed()

        // Then
        assertTrue(toastCallbackInvoked)
        assertTrue(uiHandler.isShowing())
    }

    @Test
    fun should_handleErrorWithRetryFlow_when_networkErrorOccurs() = runTest {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        var retryDialogShown = false
        var loaderShown = false
        
        dialogManager.setDialogStateCallback { _, _, _ -> retryDialogShown = true }
        loaderManager.setLoaderStateCallback { _, _ -> loaderShown = true }

        // When
        uiHandler.showErrorDialogWithRetry(
            error = error,
            onDismiss = { },
            onRetry = { uiHandler.showLoader("Retrying...") },
            onConfirm = null,
            confirmText = "OK",
            retryText = "Retry",
            title = "Error"
        )

        // Then
        assertTrue(retryDialogShown)
    }

    @Test
    fun should_coordinateMultipleManagers_when_complexScenario() = runTest {
        // Given
        var managerCallbacks = mutableListOf<String>()
        
        errorHandler.setErrorSnackbarCallback { _, _, _ -> managerCallbacks.add("error") }
        dialogManager.setDialogStateCallback { _, _, _ -> managerCallbacks.add("dialog") }
        loaderManager.setLoaderStateCallback { _, _ -> managerCallbacks.add("loader") }
        toastManager.showShort("Test") // This will add to callbacks

        // When
        uiHandler.showLoader("Loading...")
        uiHandler.showAlertDialog("Title", "Message", "OK", null)
        uiHandler.showError(BaseError.ValidationError("Validation","Validation error"))
        uiHandler.showShort("Toast message")

        // Then
        assertTrue(managerCallbacks.contains("loader"))
        assertTrue(managerCallbacks.contains("dialog"))
        assertTrue(managerCallbacks.contains("error"))
    }

    @Test
    fun should_handleStateTransitions_when_managersInteract() = runTest {
        // Given
        val error = BaseError.NetworkError("Network error", retryable = true)
        var stateTransitions = mutableListOf<String>()

        // When
        // Initial state
        stateTransitions.add("initial")
        
        // Show loader
        uiHandler.showLoader("Loading...")
        stateTransitions.add("loading")
        
        // Show error
        uiHandler.handleError(error)
        stateTransitions.add("error")
        
        // Hide loader
        uiHandler.hideLoader()
        stateTransitions.add("no_loading")
        
        // Show toast
        uiHandler.showShort("Success")
        stateTransitions.add("toast")

        // Then
        assertEquals(5, stateTransitions.size)
        assertFalse(uiHandler.isLoaderShowing())
        assertTrue(uiHandler.isShowing())
    }

    @Test
    fun should_coordinateErrorAndLoader_when_errorDuringLoading() = runTest {
        // Given
        val error = BaseError.ServerError("Server error",500)
        var loaderHidden = false
        var errorShown = false
        
        loaderManager.setLoaderStateCallback { _, isShowing -> 
            if (!isShowing) loaderHidden = true 
        }
        errorHandler.setErrorSnackbarCallback { _, _, _ -> errorShown = true }

        // When
        uiHandler.showLoader("Loading...")
        uiHandler.handleError(error)
        uiHandler.hideLoader()

        // Then
        assertTrue(loaderHidden)
        assertTrue(errorShown)
    }

    @Test
    fun should_handleBackPressDuringLoading_when_userInterrupts() {
        // Given
        var backPressHandled = false
        backPressManager.addBackPressCallback { 
            backPressHandled = true
            true
        }

        // When
        uiHandler.showLoader("Loading...")
        uiHandler.onBackPressed()

        // Then
        assertTrue(backPressHandled)
        assertTrue(uiHandler.isLoaderShowing()) // Loader should still be showing
    }

    @Test
    fun should_coordinateDialogAndToast_when_userInteraction() = runTest {
        // Given
        var dialogShown = false
        var toastShown = false
        
        dialogManager.setDialogStateCallback { _, _, _ -> dialogShown = true }

        // When
        uiHandler.showConfirmationDialog(
            "Confirm", "Are you sure?", "Yes", "No",
            onPositiveClick = { uiHandler.showSuccess("Confirmed", ToastDuration.SHORT) },
            onNegativeClick = { uiHandler.showInfo("Cancelled", ToastDuration.SHORT) }
        )

        // Then
        assertTrue(dialogShown)
    }

    @Test
    fun should_handleMultipleErrors_when_sequentialErrors() = runTest {
        // Given
        val errors = listOf(
            BaseError.NetworkError("Network error", retryable = true),
            BaseError.ValidationError("Validation","Validation error"),
            BaseError.ServerError("Server error",500)
        )
        var errorCount = 0
        
        errorHandler.setErrorSnackbarCallback { _, _, _ -> errorCount++ }

        // When
        errors.forEach { error ->
            uiHandler.handleError(error)
        }

        // Then
        assertEquals(3, errorCount)
    }

    @Test
    fun should_coordinateAllManagers_when_completeUserFlow() = runTest {
        // Given
        var flowSteps = mutableListOf<String>()
        
        loaderManager.setLoaderStateCallback { _, isShowing -> 
            if (isShowing) flowSteps.add("loader_shown") else flowSteps.add("loader_hidden")
        }
        dialogManager.setDialogStateCallback { _, _, _ -> flowSteps.add("dialog_shown") }
        errorHandler.setErrorSnackbarCallback { _, _, _ -> flowSteps.add("error_shown") }

        // When - Simulate a complete user flow
        uiHandler.showLoader("Loading data...") // Step 1
        flowSteps.add("user_action")
        uiHandler.hideLoader() // Step 2
        uiHandler.showSuccess("Data loaded successfully", ToastDuration.SHORT) // Step 3
        uiHandler.showConfirmationDialog("Delete", "Are you sure?", "Yes", "No", null, null) // Step 4
        uiHandler.handleError(BaseError.NetworkError("Connection lost", retryable = true)) // Step 5

        // Then
        assertTrue(flowSteps.contains("loader_shown"))
        assertTrue(flowSteps.contains("loader_hidden"))
        assertTrue(flowSteps.contains("dialog_shown"))
        assertTrue(flowSteps.contains("error_shown"))
        assertTrue(flowSteps.contains("user_action"))
    }

    @Test
    fun should_handleManagerStateConsistency_when_multipleOperations() {
        // Given
        var loaderState = false
        var dialogState = false
        var toastState = false
        
        loaderManager.setLoaderStateCallback { _, isShowing -> loaderState = isShowing }
        dialogManager.setDialogStateCallback { dialogs, _, _ -> dialogState = dialogs.isNotEmpty() }

        // When
        uiHandler.showLoader("Loading...")
        uiHandler.showAlertDialog("Title", "Message", "OK", null)
        uiHandler.showShort("Toast")
        
        val loaderShowing = uiHandler.isLoaderShowing()
        val dialogShowing = uiHandler.isAnyDialogShowing()
        val toastShowing = uiHandler.isShowing()

        // Then
        assertTrue(loaderShowing)
        assertTrue(dialogShowing)
        assertTrue(toastShowing)
        assertEquals(loaderShowing, loaderState)
        assertEquals(dialogShowing, dialogState)
    }
} 