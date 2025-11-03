package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing
import com.yfy.basearchitecture.core.designsystem.theme.theme.error
import com.yfy.basearchitecture.core.designsystem.theme.theme.onError
import com.yfy.basearchitecture.core.designsystem.theme.theme.onPrimary
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSecondary
import com.yfy.basearchitecture.core.designsystem.theme.theme.onTertiary
import com.yfy.basearchitecture.core.designsystem.theme.theme.primary
import com.yfy.basearchitecture.core.designsystem.theme.theme.secondary
import com.yfy.basearchitecture.core.designsystem.theme.theme.tertiary

enum class YfySnackbarType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

@Composable
fun YfySnackbar(
    message: String,
    type: YfySnackbarType = YfySnackbarType.INFO,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    val backgroundColor = when (type) {
        YfySnackbarType.SUCCESS -> primary
        YfySnackbarType.ERROR -> error
        YfySnackbarType.WARNING -> tertiary
        YfySnackbarType.INFO -> secondary
    }

    val contentColor = when (type) {
        YfySnackbarType.SUCCESS -> onPrimary
        YfySnackbarType.ERROR -> onError
        YfySnackbarType.WARNING -> onTertiary
        YfySnackbarType.INFO -> onSecondary
    }

    Snackbar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(YfySpacing.md),
        containerColor = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                it()
                Spacer(modifier = Modifier.width(YfySpacing.sm))
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            action?.let {
                Spacer(modifier = Modifier.width(YfySpacing.sm))
                it()
            }
        }
    }
}

@Composable
fun YfySnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun YfySnackbarSuccessPreview() {
    YfySnackbar(
        message = "Success message",
        type = YfySnackbarType.SUCCESS
    )
}

@Preview(showBackground = true)
@Composable
fun YfySnackbarErrorPreview() {
    YfySnackbar(
        message = "Error message",
        type = YfySnackbarType.ERROR
    )
}

@Preview(showBackground = true)
@Composable
fun YfySnackbarWarningPreview() {
    YfySnackbar(
        message = "Warning message",
        type = YfySnackbarType.WARNING
    )
}

@Preview(showBackground = true)
@Composable
fun YfySnackbarInfoPreview() {
    YfySnackbar(
        message = "Info message",
        type = YfySnackbarType.INFO
    )
} 