package com.yfy.basearchitecture.core.ui.api.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Base ViewModel for Activities
 * Provides common functionality for Activity ViewModels including error handling, analytics, and lifecycle management
 */
abstract class BaseViewModel : ViewModel() {
    @Inject lateinit var resourceProvider: ResourceProvider
    @Inject lateinit var uiHandler: UiHandler
    @Inject lateinit var analytics: AnalyticsProvider

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<BaseError?>(null)
    val error: StateFlow<BaseError?> = _error.asStateFlow()

    protected fun setLoading(loading: Boolean) { 
        _isLoading.value = loading 
    }

    protected fun setError(error: BaseError?) { 
        _error.value = error 
    }

    // Error management
    fun handleError(error: BaseError, showToast: Boolean = true, logOnly: Boolean = false) {
        if (logOnly) {
            uiHandler.logError(error)
        } else if (showToast) {
            uiHandler.showError(error)
        }
        setError(error)
    }

    fun clearError() {
        setError(null)
    }

    // Analytics methods
    fun logScreen(screenName: String) {
        analytics.logScreen(screenName)
    }

    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        analytics.logEvent(eventName, parameters)
    }

    // Async operations with loading state
    protected fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                setLoading(true)
                block()
            } catch (e: Exception) {
                handleError(BaseError.UnknownError(e.message ?: "Unknown error", e))
            } finally {
                setLoading(false)
            }
        }
    }

    // Safe async operations
    protected fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(BaseError.UnknownError(e.message ?: "Unknown error", e))
            }
        }
    }

    // Activity lifecycle events
    open fun onActivityCreated() {
        logScreen(this::class.java.simpleName)
    }

    open fun onActivityResumed() {
        // Override in subclasses if needed
    }

    open fun onActivityPaused() {
        // Override in subclasses if needed
    }

    open fun onActivityDestroyed() {
        // Override in subclasses if needed
    }
} 