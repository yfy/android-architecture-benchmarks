package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yfy.basearchitecture.core.designsystem.theme.theme.onPrimary

enum class YfyLoadingSize {
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun YfyLoadingIndicator(
    modifier: Modifier = Modifier,
    size: YfyLoadingSize = YfyLoadingSize.MEDIUM,
    isFullScreen: Boolean = false
) {
    val indicatorSize = when (size) {
        YfyLoadingSize.SMALL -> 24.dp
        YfyLoadingSize.MEDIUM -> 32.dp
        YfyLoadingSize.LARGE -> 48.dp
    }

    val containerModifier = if (isFullScreen) {
        modifier.fillMaxSize()
    } else {
        modifier
    }

    Box(
        modifier = if (isFullScreen) {
            containerModifier.background(Color.Black.copy(alpha = 0.5f))
        } else {
            containerModifier
        },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            color = onPrimary,
            strokeWidth = when (size) {
                YfyLoadingSize.SMALL -> 2.dp
                YfyLoadingSize.MEDIUM -> 3.dp
                YfyLoadingSize.LARGE -> 4.dp
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun YfyLoadingIndicatorPreview() {
    YfyLoadingIndicator(
        size = YfyLoadingSize.MEDIUM
    )
}

@Preview(showBackground = true)
@Composable
fun YfyLoadingIndicatorFullScreenPreview() {
    YfyLoadingIndicator(
        size = YfyLoadingSize.LARGE,
        isFullScreen = true
    )
}