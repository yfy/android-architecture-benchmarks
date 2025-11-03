package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSurface
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSurfaceVariant
import com.yfy.basearchitecture.core.designsystem.theme.theme.primary
import com.yfy.basearchitecture.core.designsystem.theme.theme.primaryContainer
import com.yfy.basearchitecture.core.designsystem.theme.theme.surfaceVariant

@Composable
fun YfySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = YfySpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = primary,
                checkedTrackColor = primaryContainer,
                uncheckedThumbColor = onSurfaceVariant,
                uncheckedTrackColor = surfaceVariant
            )
        )
        if (label != null) {
            Spacer(modifier = Modifier.width(YfySpacing.sm))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    onSurface
                } else {
                    onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YfySwitchPreview() {
    var checked by remember { mutableStateOf(false) }
    YfySwitch(
        checked = checked,
        onCheckedChange = { checked = it },
        label = "Switch Label"
    )
}

@Preview(showBackground = true)
@Composable
fun YfySwitchDisabledPreview() {
    YfySwitch(
        checked = false,
        onCheckedChange = {},
        enabled = false,
        label = "Disabled Switch"
    )
} 