package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing
import com.yfy.basearchitecture.core.designsystem.theme.theme.error
import com.yfy.basearchitecture.core.designsystem.theme.theme.onError
import com.yfy.basearchitecture.core.designsystem.theme.theme.onPrimary
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSecondary
import com.yfy.basearchitecture.core.designsystem.theme.theme.primary
import com.yfy.basearchitecture.core.designsystem.theme.theme.secondary

enum class YfyButtonVariant {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    DANGER
}

@Composable
fun YfyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: YfyButtonVariant = YfyButtonVariant.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val colors = when (variant) {
        YfyButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = primary,
            contentColor = onPrimary
        )
        YfyButtonVariant.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = secondary,
            contentColor = onSecondary
        )
        YfyButtonVariant.TERTIARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = primary
        )
        YfyButtonVariant.DANGER -> ButtonDefaults.buttonColors(
            containerColor = error,
            contentColor = onError
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled && !isLoading,
        contentPadding = contentPadding,
        colors = colors,
        content = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(YfySpacing.xs),
                    color = onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                content()
            }
        }
    )
}

@Composable
fun YfyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: YfyButtonVariant = YfyButtonVariant.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    YfyButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        enabled = enabled,
        isLoading = isLoading
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = when (variant) {
                YfyButtonVariant.PRIMARY -> onPrimary
                YfyButtonVariant.SECONDARY -> onSecondary
                YfyButtonVariant.TERTIARY -> primary
                YfyButtonVariant.DANGER -> onError
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun YfyButtonPreview() {
    YfyButton(
        text = "Primary Button",
        onClick = {},
        variant = YfyButtonVariant.PRIMARY
    )
}

@Preview(showBackground = true)
@Composable
fun YfyButtonSecondaryPreview() {
    YfyButton(
        text = "Secondary Button",
        onClick = {},
        variant = YfyButtonVariant.SECONDARY
    )
}

@Preview(showBackground = true)
@Composable
fun YfyButtonTertiaryPreview() {
    YfyButton(
        text = "Tertiary Button",
        onClick = {},
        variant = YfyButtonVariant.TERTIARY
    )
}

@Preview(showBackground = true)
@Composable
fun YfyButtonDangerPreview() {
    YfyButton(
        text = "Danger Button",
        onClick = {},
        variant = YfyButtonVariant.DANGER
    )
} 