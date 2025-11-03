package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yfy.basearchitecture.core.designsystem.R
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfyShape
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YfyDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    text: String? = null,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                content?.invoke()
            }
        },
        confirmButton = confirmButton?: {},
        dismissButton = dismissButton,
        shape = YfyShape.medium
    )
}

@Composable
fun YfyDialogButtons(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(R.string.dialog_confirm),
    dismissText: String = stringResource(R.string.dialog_cancel)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YfySpacing.md),
        horizontalArrangement = Arrangement.spacedBy(YfySpacing.sm)
    ) {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = dismissText)
        }
        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = confirmText)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YfyFullScreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = YfyShape.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YfyDialogPreview() {
    YfyDialog(
        onDismissRequest = {},
        title = stringResource(R.string.dialog_title),
        text = stringResource(R.string.dialog_message),
        confirmButton = {
            YfyDialogButtons(
                onConfirm = {},
                onDismiss = {}
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun YfyFullScreenDialogPreview() {
    YfyFullScreenDialog(
        onDismissRequest = {}
    ) {
        Column(
            modifier = Modifier.padding(YfySpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.dialog_fullscreen_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(YfySpacing.md))
            Text(
                text = stringResource(R.string.dialog_fullscreen_message),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 