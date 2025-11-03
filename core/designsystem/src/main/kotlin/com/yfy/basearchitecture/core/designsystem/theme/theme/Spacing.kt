package com.yfy.basearchitecture.core.designsystem.theme.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object YfySpacing {
    // Base spacing unit
    val base: Dp = 8.dp

    // Extra small spacing
    val xs: Dp = base / 2 // 4.dp
    val xs2: Dp = base / 4 // 2.dp

    // Small spacing
    val sm: Dp = base // 8.dp
    val sm2: Dp = base * 1.5f // 12.dp

    // Medium spacing
    val md: Dp = base * 2 // 16.dp
    val md2: Dp = base * 3 // 24.dp

    // Large spacing
    val lg: Dp = base * 4 // 32.dp
    val lg2: Dp = base * 5 // 40.dp

    // Extra large spacing
    val xl: Dp = base * 6 // 48.dp
    val xl2: Dp = base * 8 // 64.dp

    // Screen padding
    val screenPadding: Dp = md // 16.dp
    val screenPaddingLarge: Dp = lg // 32.dp

    // Component spacing
    val buttonPadding: Dp = sm // 8.dp
    val cardPadding: Dp = md // 16.dp
    val listItemSpacing: Dp = sm // 8.dp
    val dividerSpacing: Dp = md // 16.dp

    // Icon spacing
    val iconSize: Dp = md // 16.dp
    val iconSizeLarge: Dp = lg // 32.dp
    val iconSpacing: Dp = xs // 4.dp

    // Text spacing
    val textSpacing: Dp = xs // 4.dp
    val paragraphSpacing: Dp = md // 16.dp

    // Input spacing
    val inputPadding: Dp = sm // 8.dp
    val inputSpacing: Dp = xs // 4.dp

    // Navigation spacing
    val navItemSpacing: Dp = md // 16.dp
    val navIconSpacing: Dp = xs // 4.dp

    // Dialog spacing
    val dialogPadding: Dp = md // 16.dp
    val dialogSpacing: Dp = sm // 8.dp

    // Bottom sheet spacing
    val bottomSheetPadding: Dp = md // 16.dp
    val bottomSheetSpacing: Dp = sm // 8.dp
}

@Composable
private fun SpacingItem(name: String, spacing: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = primary,
            modifier = Modifier.width(150.dp)
        )
        Box(
            modifier = Modifier
                .width(spacing)
                .height(24.dp)
                .background(
                    color = primary,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Text(
            text = "${spacing.value}dp",
            style = MaterialTheme.typography.labelMedium,
            color = primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(name = "Spacing Showcase", showBackground = true)
@Composable
fun YfySpacingPreview() {
    YfyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Spacing System",
                style = MaterialTheme.typography.headlineMedium,
                color = primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SpacingItem("xs2", YfySpacing.xs2)
            SpacingItem("xs", YfySpacing.xs)
            SpacingItem("sm", YfySpacing.sm)
            SpacingItem("sm2", YfySpacing.sm2)
            SpacingItem("md", YfySpacing.md)
            SpacingItem("md2", YfySpacing.md2)
            SpacingItem("lg", YfySpacing.lg)
            SpacingItem("lg2", YfySpacing.lg2)
            SpacingItem("xl", YfySpacing.xl)
            SpacingItem("xl2", YfySpacing.xl2)
            SpacingItem("screenPadding", YfySpacing.screenPadding)
            SpacingItem("screenPaddingLarge", YfySpacing.screenPaddingLarge)
            SpacingItem("buttonPadding", YfySpacing.buttonPadding)
            SpacingItem("cardPadding", YfySpacing.cardPadding)
            SpacingItem("listItemSpacing", YfySpacing.listItemSpacing)
            SpacingItem("dividerSpacing", YfySpacing.dividerSpacing)
            SpacingItem("iconSize", YfySpacing.iconSize)
            SpacingItem("iconSizeLarge", YfySpacing.iconSizeLarge)
            SpacingItem("iconSpacing", YfySpacing.iconSpacing)
            SpacingItem("textSpacing", YfySpacing.textSpacing)
            SpacingItem("paragraphSpacing", YfySpacing.paragraphSpacing)
            SpacingItem("inputPadding", YfySpacing.inputPadding)
            SpacingItem("inputSpacing", YfySpacing.inputSpacing)
            SpacingItem("navItemSpacing", YfySpacing.navItemSpacing)
            SpacingItem("navIconSpacing", YfySpacing.navIconSpacing)
            SpacingItem("dialogPadding", YfySpacing.dialogPadding)
            SpacingItem("dialogSpacing", YfySpacing.dialogSpacing)
            SpacingItem("bottomSheetPadding", YfySpacing.bottomSheetPadding)
            SpacingItem("bottomSheetSpacing", YfySpacing.bottomSheetSpacing)
        }
    }
}