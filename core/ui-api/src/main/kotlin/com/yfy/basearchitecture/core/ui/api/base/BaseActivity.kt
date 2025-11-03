package com.yfy.basearchitecture.core.ui.api.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.yfy.basearchitecture.core.ui.api.handler.BaseError
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Base Activity class for Compose-based Activities
 * Provides minimal functionality for activities that use BaseScreen and BaseViewModel
 * Most UI logic is handled by BaseScreen and BaseViewModel
 */
abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var uiHandler: UiHandler

    protected abstract fun getViewModelClass(): Class<out BaseViewModel>

    protected fun getViewModel(): BaseViewModel {
        return ViewModelProvider(this)[getViewModelClass()]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBackPressHandler()
        setupViewModelObservers()
        initializeActivity()
    }

    override fun onResume() {
        super.onResume()
        getViewModel().onActivityResumed()
    }

    override fun onPause() {
        super.onPause()
        getViewModel().onActivityPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        getViewModel().onActivityDestroyed()
    }

    /**
     * Setup back press handler
     */
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        })
    }

    /**
     * Setup ViewModel observers
     */
    private fun setupViewModelObservers() {
        val viewModel = getViewModel()
        
        // Observe loading state
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                if (isLoading) {
                    uiHandler.showLoader()
                } else {
                    uiHandler.hideLoader()
                }
            }
        }
        
        // Observe error state
        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let { handleError(it) }
            }
        }
        
        // Call activity created
        viewModel.onActivityCreated()
    }

    /**
     * Initialize activity - override in subclasses
     */
    abstract fun initializeActivity()

    /**
     * Handle back press - override in subclasses if needed
     */
    protected open fun onBackPress() {
        if (!uiHandler.onBackPressed()) {
            finish()
        }
    }

    /**
     * Handle errors - override in subclasses if needed
     */
    protected open fun handleError(error: BaseError) {
        uiHandler.handleError(error)
    }

    /**
     * Set content with Compose
     */
    protected fun setComposeContent(content: @Composable () -> Unit) {
        setContent {
            content()
        }
    }
} 