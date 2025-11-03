package com.yfy.basearchitecture.core.ui.api.base

import androidx.compose.runtime.State

interface BasePresenter {
    val isLoading: State<Boolean>
    val dialogState: State<DialogState?>
    val bottomSheetState: State<BottomSheetState?>

    fun dismissDialog()
    fun dismissBottomSheet()
    fun navigateUp()

    fun onViewCreated()
    fun onViewDestroyed()
}