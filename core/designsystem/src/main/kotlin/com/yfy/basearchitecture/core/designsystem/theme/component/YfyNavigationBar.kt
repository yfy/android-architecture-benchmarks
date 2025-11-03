package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.yfy.basearchitecture.core.designsystem.theme.icon.YfyIcons
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSurface
import com.yfy.basearchitecture.core.designsystem.theme.theme.onSurfaceVariant
import com.yfy.basearchitecture.core.designsystem.theme.theme.primary
import com.yfy.basearchitecture.core.designsystem.theme.theme.surface
import com.yfy.basearchitecture.core.designsystem.theme.theme.surfaceVariant

data class YfyNavigationItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YfyNavigationBar(
    items: List<YfyNavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = surface,
        contentColor = onSurface
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex && item.selectedIcon != null) {
                            item.selectedIcon
                        } else {
                            item.icon
                        },
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primary,
                    selectedTextColor = primary,
                    indicatorColor = surfaceVariant,
                    unselectedIconColor = onSurfaceVariant,
                    unselectedTextColor = onSurfaceVariant
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun YfyNavigationBarPreview() {
    var selectedItemIndex by remember { mutableStateOf(0) }
    val items = listOf(
        YfyNavigationItem(
            title = "Home",
            icon = YfyIcons.Home,
            selectedIcon = YfyIcons.HomeOutlined
        ),
        YfyNavigationItem(
            title = "Profile",
            icon = YfyIcons.Profile,
            selectedIcon = YfyIcons.ProfileOutlined
        ),
        YfyNavigationItem(
            title = "Settings",
            icon = YfyIcons.Settings,
            selectedIcon = YfyIcons.SettingsOutlined
        )
    )
    YfyNavigationBar(
        items = items,
        selectedItemIndex = selectedItemIndex,
        onItemSelected = { selectedItemIndex = it }
    )
}

@Composable
fun RowScope.YfyNavigationBarItem(
    item: YfyNavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected && item.selectedIcon != null) {
                    item.selectedIcon
                } else {
                    item.icon
                },
                contentDescription = item.title
            )
        },
        label = { Text(text = item.title) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
} 