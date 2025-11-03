package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yfy.basearchitecture.core.designsystem.theme.icon.YfyIcons
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfyShape
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfyTypography
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSurface
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSurfaceVariant
import com.yfy.basearchitecture.core.designsystem.theme.theme.surface

enum class YfyListItemVariant {
    ONE_LINE,
    TWO_LINE,
    THREE_LINE
}

@Composable
fun YfyListItem(
    title: String,
    modifier: Modifier = Modifier,
    variant: YfyListItemVariant = YfyListItemVariant.ONE_LINE,
    subtitle: String? = null,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = YfyShape.medium,
        color = surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YfySpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(YfySpacing.md))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = YfyTypography.bodyLarge,
                    color = onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (variant != YfyListItemVariant.ONE_LINE && subtitle != null) {
                    Spacer(modifier = Modifier.height(YfySpacing.xs2))
                    Text(
                        text = subtitle,
                        style = YfyTypography.bodyMedium,
                        color = onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (variant == YfyListItemVariant.THREE_LINE && supportingText != null) {
                    Spacer(modifier = Modifier.height(YfySpacing.xs2))
                    Text(
                        text = supportingText,
                        style = YfyTypography.bodySmall,
                        color = onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(YfySpacing.md))
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YfyListItemOneLinePreview() {
    YfyListItem(
        title = "One Line Item",
        variant = YfyListItemVariant.ONE_LINE,
        leadingIcon = YfyIcons.Profile,
        trailingIcon = YfyIcons.Forward
    )
}

@Preview(showBackground = true)
@Composable
fun YfyListItemTwoLinePreview() {
    YfyListItem(
        title = "Two Line Item",
        variant = YfyListItemVariant.TWO_LINE,
        subtitle = "Subtitle",
        leadingIcon = YfyIcons.Profile,
        trailingIcon = YfyIcons.Forward
    )
}

@Preview(showBackground = true)
@Composable
fun YfyListItemThreeLinePreview() {
    YfyListItem(
        title = "Three Line Item",
        variant = YfyListItemVariant.THREE_LINE,
        subtitle = "Subtitle",
        supportingText = "Supporting Text",
        leadingIcon = YfyIcons.Profile,
        trailingIcon = YfyIcons.Forward
    )
} 