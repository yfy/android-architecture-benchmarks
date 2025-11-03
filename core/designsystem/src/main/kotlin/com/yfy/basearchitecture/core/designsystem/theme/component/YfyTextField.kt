package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yfy.basearchitecture.core.designsystem.R
import com.yfy.basearchitecture.core.designsystem.theme.icon.YfyIcons
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfySpacing

@Composable
fun YfyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val visualTransformation = when {
        isPassword && !isPasswordVisible -> PasswordVisualTransformation()
        else -> VisualTransformation.None
    }

    val trailingIconContent = when {
        isPassword -> {
            {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) YfyIcons.Visibility else YfyIcons.VisibilityOff,
                        contentDescription = if (isPasswordVisible) stringResource(R.string.textfield_hide_password) else stringResource(R.string.textfield_show_password)
                    )
                }
            }
        }
        value.isNotEmpty() -> {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = YfyIcons.Close,
                        contentDescription = stringResource(R.string.textfield_clear_text)
                    )
                }
            }
        }
        else -> trailingIcon
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = YfySpacing.xs),
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
        visualTransformation = visualTransformation,
        isError = isError,
        supportingText = errorMessage?.let { { Text(text = it) } },
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIconContent,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Preview(showBackground = true)
@Composable
fun YfyTextFieldPreview() {
    YfyTextField(
        value = stringResource(R.string.textfield_sample_text),
        onValueChange = {},
        label = stringResource(R.string.textfield_label),
        placeholder = stringResource(R.string.textfield_placeholder)
    )
}

@Preview(showBackground = true)
@Composable
fun YfyTextFieldPasswordPreview() {
    YfyTextField(
        value = "password123",
        onValueChange = {},
        label = stringResource(R.string.textfield_password),
        isPassword = true
    )
}

@Preview(showBackground = true)
@Composable
fun YfyTextFieldErrorPreview() {
    YfyTextField(
        value = "Invalid Input",
        onValueChange = {},
        label = stringResource(R.string.textfield_error_field),
        isError = true,
        errorMessage = stringResource(R.string.textfield_required)
    )
} 