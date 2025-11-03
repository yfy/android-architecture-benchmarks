package com.yfy.basearchitecture.core.designsystem.theme.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val primary = Color(0xFF5A5A5A)
val onPrimary = Color(0xFFFFFFFF)
val primaryContainer = Color(0xFFD9D9D9)
val onPrimaryContainer = Color(0xFF1C1C1C)

val primaryDark = Color(0xFFB5B5B5)
val onPrimaryDark = Color(0xFF1A1A1A)
val primaryContainerDark = Color(0xFF3A3A3A)
val onPrimaryContainerDark = Color(0xFFE8E8E8)

val secondary = Color(0xFF7A7A7A)
val onSecondary = Color(0xFFFFFFFF)
val secondaryContainer = Color(0xFFE2E2E2)
val onSecondaryContainer = Color(0xFF2A2A2A)

val secondaryDark = Color(0xFF9C9C9C)
val onSecondaryDark = Color(0xFF1A1A1A)
val secondaryContainerDark = Color(0xFF4C4C4C)
val onSecondaryContainerDark = Color(0xFFF0F0F0)

val tertiary = Color(0xFF9EA2A7)
val onTertiary = Color(0xFF1A1A1A)
val tertiaryContainer = Color(0xFFE6E8EB)
val onTertiaryContainer = Color(0xFF34383D)

val tertiaryDark = Color(0xFFB0B4B9)
val onTertiaryDark = Color(0xFF1A1A1A)
val tertiaryContainerDark = Color(0xFF52565A)
val onTertiaryContainerDark = Color(0xFFEDEDED)

val error = Color(0xFFB3261E)
val onError = Color(0xFFFFFFFF)
val errorContainer = Color(0xFFF9DEDC)
val onErrorContainer = Color(0xFF410E0B)

val errorDark = Color(0xFFF2B8B5)
val onErrorDark = Color(0xFF601410)
val errorContainerDark = Color(0xFF8C1D18)
val onErrorContainerDark = Color(0xFFF9DEDC)

val background = Color(0xFFF6F6F6)
val onBackground = Color(0xFF1C1C1C)
val surface = Color(0xFFFFFFFF)
val onSurface = Color(0xFF1C1C1C)
val surfaceVariant = Color(0xFFE4E4E4)
val onSurfaceVariant = Color(0xFF4E4E4E)
val outline = Color(0xFFB5B5B5)
val inverseOnSurface = Color(0xFFF0F0F0)
val inverseSurface = Color(0xFF2C2C2C)
val inversePrimary = Color(0xFF9C9C9C)
val surfaceTint = Color(0xFF7A7A7A)
val outlineVariant = Color(0xFFD8D8D8)
val scrim = Color(0xFF000000)

val backgroundDark = Color(0xFF121212)
val onBackgroundDark = Color(0xFFEAEAEA)
val surfaceDark = Color(0xFF1C1C1C)
val onSurfaceDark = Color(0xFFE0E0E0)
val surfaceVariantDark = Color(0xFF2A2A2A)
val onSurfaceVariantDark = Color(0xFFB5B5B5)
val outlineDark = Color(0xFF6E6E6E)
val inverseOnSurfaceDark = Color(0xFF1A1A1A)
val inverseSurfaceDark = Color(0xFFE0E0E0)
val inversePrimaryDark = Color(0xFFB5B5B5)
val surfaceTintDark = Color(0xFF9C9C9C)
val outlineVariantDark = Color(0xFF444444)
val scrimDark = Color(0xFF000000)

val success = Color(0xFF5E9C76)
val successDark = Color(0xFF7ABF91)
val warning = Color(0xFFD4A056)
val warningDark = Color(0xFFE6B97B)
val info = Color(0xFF6B8BAE)
val infoDark = Color(0xFF8EABC8)

val textPrimary = Color(0xFF1A1A1A)
val textSecondary = Color(0xFF4E4E4E)
val textTertiary = Color(0xFF7A7A7A)
val textDisabled = Color(0xFFB5B5B5)

val textPrimaryDark = Color(0xFFE0E0E0)
val textSecondaryDark = Color(0xFFB5B5B5)
val textTertiaryDark = Color(0xFF9E9E9E)
val textDisabledDark = Color(0xFF6E6E6E)



val gray10 = Color(0xFFFAFAFA)
val gray20 = Color(0xFFF5F5F5)
val gray30 = Color(0xFFEEEEEE)
val gray40 = Color(0xFFE0E0E0)
val gray50 = Color(0xFFBDBDBD)
val gray60 = Color(0xFF9E9E9E)
val gray70 = Color(0xFF757575)
val gray80 = Color(0xFF616161)
val gray90 = Color(0xFF424242)
val gray100 = Color(0xFF212121)

val pink10 = Color(0xFFFCE4EC)
val pink20 = Color(0xFFF8BBD0)
val pink30 = Color(0xFFF48FB1)
val pink40 = Color(0xFFF06292)
val pink50 = Color(0xFFEC407A)
val pink60 = Color(0xFFE91E63)
val pink70 = Color(0xFFD81B60)
val pink80 = Color(0xFFC2185B)
val pink90 = Color(0xFFAD1457)
val pink100 = Color(0xFF880E4F)

val orange10 = Color(0xFFFFE0B2)
val orange20 = Color(0xFFFFCC80)
val orange30 = Color(0xFFFFB74D)
val orange40 = Color(0xFFFFA726)
val orange50 = Color(0xFFFF9800)
val orange60 = Color(0xFFFB8C00)
val orange70 = Color(0xFFF57C00)
val orange80 = Color(0xFFEF6C00)
val orange90 = Color(0xFFE65100)
val orange100 = Color(0xFFBF360C)

val purple10 = Color(0xFFF3E5F5)
val purple20 = Color(0xFFE1BEE7)
val purple30 = Color(0xFFCE93D8)
val purple40 = Color(0xFFBA68C8)
val purple50 = Color(0xFFAB47BC)
val purple60 = Color(0xFF9C27B0)
val purple70 = Color(0xFF8E24AA)
val purple80 = Color(0xFF7B1FA2)
val purple90 = Color(0xFF6A1B9A)
val purple100 = Color(0xFF4A148C)

val green10 = Color(0xFFE8F5E9)
val green20 = Color(0xFFC8E6C9)
val green30 = Color(0xFFA5D6A7)
val green40 = Color(0xFF81C784)
val green50 = Color(0xFF66BB6A)
val green60 = Color(0xFF4CAF50)
val green70 = Color(0xFF43A047)
val green80 = Color(0xFF388E3C)
val green90 = Color(0xFF2E7D32)
val green100 = Color(0xFF1B5E20)

val red10 = Color(0xFFFFEBEE)
val red20 = Color(0xFFFFCDD2)
val red30 = Color(0xFFEF9A9A)
val red40 = Color(0xFFE57373)
val red50 = Color(0xFFEF5350)
val red60 = Color(0xFFF44336)
val red70 = Color(0xFFE53935)
val red80 = Color(0xFFD32F2F)
val red90 = Color(0xFFC62828)
val red100 = Color(0xFFB71C1C)

val blue10 = Color(0xFFE3F2FD)
val blue20 = Color(0xFFBBDEFB)
val blue30 = Color(0xFF90CAF9)
val blue40 = Color(0xFF64B5F6)
val blue50 = Color(0xFF42A5F5)
val blue60 = Color(0xFF2196F3)
val blue70 = Color(0xFF1E88E5)
val blue80 = Color(0xFF1976D2)
val blue90 = Color(0xFF1565C0)
val blue100 = Color(0xFF0D47A1)

@Composable
private fun ColorSchemePreview() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "YFY Color Scheme",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            ColorSection(title = "Primary Colors (Pink)") {
                ColorItem("Primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                ColorItem("Primary Container", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                ColorItem("Inverse Primary", MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.onPrimary)
            }
        }

        item {
            ColorSection(title = "Secondary Colors (Orange)") {
                ColorItem("Secondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
                ColorItem("Secondary Container", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }

        item {
            ColorSection(title = "Tertiary Colors (Purple)") {
                ColorItem("Tertiary", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
                ColorItem("Tertiary Container", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
            }
        }

        item {
            ColorSection(title = "Surface Colors") {
                ColorItem("Surface", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
                ColorItem("Surface Variant", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                ColorItem("Background", MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground)
            }
        }

        item {
            ColorSection(title = "Error Colors") {
                ColorItem("Error", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
                ColorItem("Error Container", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

@Composable
private fun ColorSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun ColorItem(
    name: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}

@Preview(name = "Color Scheme Light", showBackground = true, heightDp = 800)
@Composable
fun YfyColorSchemePreview() {
    YfyTheme(darkTheme = false) {
        ColorSchemePreview()
    }
}

@Preview(name = "Color Scheme Dark", showBackground = true, heightDp = 800)
@Composable
fun YfyColorSchemeDarkPreview() {
    YfyTheme(darkTheme = true) {
        ColorSchemePreview()
    }
}