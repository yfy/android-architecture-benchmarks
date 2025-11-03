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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
fun BaseScreenScaffold(
    viewModel: BaseComposeViewModel,
    screenName: String,
    modifier: Modifier = Modifier,
    enableBackHandler: Boolean = true,
    showToolbar: Boolean = false,
    toolbarTitle: String? = null,
    content: @Composable () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    val bottomSheetState by viewModel.bottomSheetState.collectAsState()

    YfyTheme {
        // Analytics
        LaunchedEffect(Unit) {
            viewModel.logScreen(screenName)
        }
//
        // Back handling
        if (enableBackHandler) {
            BackHandler {
                viewModel.navigationManager.navigateUp()
            }
        }

        Scaffold(
            topBar = {
                if (showToolbar) {
                    YfyTopAppBar(
                        title = toolbarTitle ?: screenName,
                        onBackClick = { viewModel.navigationManager.navigateUp() }
                    )
                }
            }
        ) { padding ->
            Box(modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                )
            {
                content()

                if (isLoading) {
                    YfyLoadingIndicator(isFullScreen = true)
                }

                bottomSheetState?.let { sheet ->
                    when (sheet) {
                        is BottomSheetState.Custom -> YfyBottomSheet(
                            onDismissRequest = { viewModel.dismissBottomSheet() },
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
                    onDismissRequest = dialog.onDismiss,
                    title = dialog.title,
                    text = dialog.message
                )
                is DialogState.Error -> YfyDialog(
                    onDismissRequest = dialog.onDismiss,
                    title = dialog.title,
                    text = dialog.message
                )
                is DialogState.Confirmation -> YfyDialog(
                    onDismissRequest = dialog.onDismiss,
                    title = dialog.title,
                    text = dialog.message,
                    confirmButton = {
                        TextButton(onClick = dialog.onConfirm) {
                            Text(stringResource(R.string.action_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = dialog.onDismiss) {
                            Text(stringResource(R.string.action_cancel))
                        }
                    }
                )
            }
        }
    }
}