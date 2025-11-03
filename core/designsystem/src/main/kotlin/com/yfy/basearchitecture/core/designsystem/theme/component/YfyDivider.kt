package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing

enum class YfyDividerVariant {
    FULL,
    INDENTED,
    OUTDENTED
}

@Composable
fun YfyDivider(
    modifier: Modifier = Modifier,
    variant: YfyDividerVariant = YfyDividerVariant.FULL,
    thickness: Int = 1
) {
    val padding = when (variant) {
        YfyDividerVariant.FULL -> 0.dp
        YfyDividerVariant.INDENTED -> YfySpacing.md
        YfyDividerVariant.OUTDENTED -> YfySpacing.md
    }

    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        thickness = thickness.dp,
        color = MaterialTheme.colorScheme.outline
    )
}

@Preview(showBackground = true)
@Composable
fun YfyDividerFullPreview() {
    YfyDivider(variant = YfyDividerVariant.FULL)
}

@Preview(showBackground = true)
@Composable
fun YfyDividerIndentedPreview() {
    YfyDivider(variant = YfyDividerVariant.INDENTED)
}

@Preview(showBackground = true)
@Composable
fun YfyDividerOutdentedPreview() {
    YfyDivider(variant = YfyDividerVariant.OUTDENTED)
} 