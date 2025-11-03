package com.yfy.basearchitecture.core.ui.api.base

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.R
import com.yfy.basearchitecture.core.ui.api.extensions.toBaseError
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Base ViewModel for Compose screens
 * Provides state management, loading, dialogs, analytics, and navigation for Compose UI
 */
abstract class BaseComposeViewModel : ViewModel() {
    @Inject lateinit var resourceProvider: ResourceProvider
    @Inject lateinit var uiHandler: UiHandler
    @Inject lateinit var analytics: AnalyticsProvider
    @Inject lateinit var navigationManager: NavigationManager

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState: StateFlow<DialogState?> = _dialogState.asStateFlow()

    private val _bottomSheetState = MutableStateFlow<BottomSheetState?>(null)
    val bottomSheetState: StateFlow<BottomSheetState?> = _bottomSheetState.asStateFlow()

    protected fun setLoading(loading: Boolean) { _isLoading.value = loading }
    protected fun showDialog(dialog: DialogState) { _dialogState.value = dialog }
    fun dismissDialog() { _dialogState.value = null }
    protected fun showBottomSheet(sheet: BottomSheetState) { _bottomSheetState.value = sheet }
    fun dismissBottomSheet() { _bottomSheetState.value = null }

    val exceptionHandler = CoroutineExceptionHandler{ _, t -> handleError(t)}

    // Error management
    fun handleError(error: BaseError, showDialog: Boolean = true, logOnly: Boolean = false, onDismiss: () -> Unit = {}) {
        if (logOnly) {
            uiHandler.logError(error)
        } else if (showDialog) {
            showDialog(DialogState.Error(
                title = resourceProvider.getString(R.string.error_title),
                message = error.getUserMessage(),
                onDismiss = {
                    onDismiss.invoke()
                    dismissDialog()
                }
            ))
        } else {
            uiHandler.showError(error)
        }
    }

    /**
     * Handle error from Throwable with automatic conversion to BaseError
     */
    fun handleError(throwable: Throwable, showDialog: Boolean = true, logOnly: Boolean = false, onDismiss: () -> Unit = {}) {
        handleError(throwable.toBaseError(), showDialog, logOnly, onDismiss)
    }

    // Analytics methods
    fun logScreen(screenName: String) {
        analytics.logScreen(screenName)
    }

    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        analytics.logEvent(eventName, parameters)
    }

}

sealed class DialogState {
    data class Info(val title: String, val message: String, val onDismiss: () -> Unit = {}) : DialogState()
    data class Error(val title: String, val message: String, val onDismiss: () -> Unit = {}) : DialogState()
    data class Confirmation(val title: String, val message: String, val onConfirm: () -> Unit, val onDismiss: () -> Unit = {}) : DialogState()
}

sealed class BottomSheetState {
    data class Custom(val content: @Composable () -> Unit) : BottomSheetState()
} 