package com.yfy.basearchitecture.core.ui.api.base

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import com.yfy.basearchitecture.core.navigation.NavigationManager
import javax.inject.Inject

abstract class BasePresenterImpl(
    protected val navigationManager: NavigationManager
) : BasePresenter {
    
    @Inject lateinit var analytics: AnalyticsProvider

    private val _isLoading = mutableStateOf(false)
    override val isLoading: State<Boolean> = _isLoading

    private val _dialogState = mutableStateOf<DialogState?>(null)
    override val dialogState: State<DialogState?> = _dialogState

    private val _bottomSheetState = mutableStateOf<BottomSheetState?>(null)
    override val bottomSheetState: State<BottomSheetState?> = _bottomSheetState

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    protected fun showDialog(dialog: DialogState) {
        _dialogState.value = dialog
    }

    override fun dismissDialog() {
        _dialogState.value = null
    }

    protected fun showBottomSheet(sheet: BottomSheetState) {
        _bottomSheetState.value = sheet
    }

    override fun dismissBottomSheet() {
        _bottomSheetState.value = null
    }

    fun logScreen(screenName: String) {
        analytics.logScreen(screenName)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context , message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateUp() {
        navigationManager.navigateUp()
    }

    override fun onViewCreated() {
        // Override if needed
    }

    override fun onViewDestroyed() {
        // Override if needed
    }
}