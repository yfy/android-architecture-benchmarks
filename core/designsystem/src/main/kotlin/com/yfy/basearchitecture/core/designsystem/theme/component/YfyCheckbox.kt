package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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

@Composable
fun YfyCheckbox(
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
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled)

        if (label != null) {
            Spacer(modifier = Modifier.width(YfySpacing.sm))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YfyCheckboxPreview() {
    var checked by remember { mutableStateOf(false) }
    YfyCheckbox(
        checked = checked,
        onCheckedChange = { checked = it },
        label = "Checkbox Label"
    )
}

@Preview(showBackground = true)
@Composable
fun YfyCheckboxDisabledPreview() {
    YfyCheckbox(
        checked = false,
        onCheckedChange = {},
        enabled = false,
        label = "Disabled Checkbox"
    )
} 