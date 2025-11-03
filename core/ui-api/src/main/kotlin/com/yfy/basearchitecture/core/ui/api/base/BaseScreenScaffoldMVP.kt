package com.yfy.basearchitecture.core.ui.api.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yfy.basearchitecture.core.designsystem.theme.component.YfyBottomSheet
import com.yfy.basearchitecture.core.designsystem.theme.component.YfyDialog
import com.yfy.basearchitecture.core.designsystem.theme.component.YfyLoadingIndicator
import com.yfy.basearchitecture.core.designsystem.theme.component.YfyTopAppBar
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfyTheme
import com.yfy.basearchitecture.core.ui.api.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreenScaffoldMvp(
    presenter: BasePresenter,
    screenName: String,
    modifier: Modifier = Modifier,
    enableBackHandler: Boolean = true,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    content: @Composable () -> Unit
) {
    val isLoading by presenter.isLoading
    val dialogState by presenter.dialogState
    val bottomSheetState by presenter.bottomSheetState

    // Lifecycle management
    DisposableEffect(Unit) {
        presenter.onViewCreated()

        onDispose {
            presenter.onViewDestroyed()
        }
    }

    YfyTheme {
        // Back handling
        if (enableBackHandler) {
            BackHandler {
                presenter.navigateUp()
            }
        }

        Scaffold(
            topBar = {
                if (showToolbar) {
                    YfyTopAppBar(
                        title = toolbarTitle ?: screenName,
                        onBackClick = { presenter.navigateUp() }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                content()

                if (isLoading) {
                    YfyLoadingIndicator(isFullScreen = true)
                }

                bottomSheetState?.let { sheet ->
                    when (sheet) {
                        is BottomSheetState.Custom -> YfyBottomSheet(
                            onDismissRequest = { presenter.dismissBottomSheet() },
                            content = sheet.content
                        )
                    }
                }
            }
        }

        // Dialogs
        dialogState?.let { dialog ->
            when (dialog) {
                is DialogState.Info -> YfyDialog(
                    onDismissRequest = { presenter.dismissDialog() },
                    title = dialog.title,
                    text = dialog.message
                )
                is DialogState.Error -> YfyDialog(
                    onDismissRequest = { presenter.dismissDialog() },
                    title = dialog.title,
                    text = dialog.message
                )
                is DialogState.Confirmation -> YfyDialog(
                    onDismissRequest = { presenter.dismissDialog() },
                    title = dialog.title,
                    text = dialog.message,
                    confirmButton = {
                        TextButton(onClick = {
                            dialog.onConfirm()
                            presenter.dismissDialog()
                        }) {
                            Text(stringResource(R.string.action_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { presenter.dismissDialog() }) {
                            Text(stringResource(R.string.action_cancel))
                        }
                    }
                )
            }
        }
    }
}