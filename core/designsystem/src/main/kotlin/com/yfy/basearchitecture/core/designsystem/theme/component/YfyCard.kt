package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfyShape
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing
import com.yfy.basearchitecture.core.designsystem.theme.theme.surface
import com.yfy.basearchitecture.core.designsystem.theme.theme.surfaceVariant

enum class YfyCardVariant {
    ELEVATED,
    FILLED,
    OUTLINED
}

@Composable
fun YfyCard(
    modifier: Modifier = Modifier,
    variant: YfyCardVariant = YfyCardVariant.ELEVATED,
    shape: Shape = YfyShape.medium,
    contentPadding: PaddingValues = PaddingValues(YfySpacing.md),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    val colors = when (variant) {
        YfyCardVariant.ELEVATED -> CardDefaults.cardColors(
            containerColor = surface
        )
        YfyCardVariant.FILLED -> CardDefaults.cardColors(
            containerColor = surfaceVariant
        )
        YfyCardVariant.OUTLINED -> CardDefaults.cardColors(
            containerColor = surface
        )
    }

    val elevation = when (variant) {
        YfyCardVariant.ELEVATED -> CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
        else -> CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = colors,
        elevation = elevation
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            contentAlignment = contentAlignment
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YfyCardElevatedPreview() {
    YfyCard(
        variant = YfyCardVariant.ELEVATED
    ) {
        Text(text = "Elevated Card")
    }
}

@Preview(showBackground = true)
@Composable
fun YfyCardFilledPreview() {
    YfyCard(
        variant = YfyCardVariant.FILLED
    ) {
        Text(text = "Filled Card")
    }
}

@Preview(showBackground = true)
@Composable
fun YfyCardOutlinedPreview() {
    YfyCard(
        variant = YfyCardVariant.OUTLINED
    ) {
        Text(text = "Outlined Card")
    }
} 