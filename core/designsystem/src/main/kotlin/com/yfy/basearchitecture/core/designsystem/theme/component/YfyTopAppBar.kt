package com.yfy.basearchitecture.core.designsystem.theme.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yfy.basearchitecture.core.designsystem.R
import com.yfy.basearchitecture.core.designsystem.theme.icon.YfyIcons

enum class YfyTopAppBarVariant {
    CENTER_ALIGNED,
    REGULAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YfyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    variant: YfyTopAppBarVariant = YfyTopAppBarVariant.REGULAR,
    onBackClick: () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = {
        IconButton(onClick = { onBackClick.invoke() }) {
            Icon(
                imageVector = YfyIcons.Back,
                contentDescription = stringResource(R.string.nav_back)
            )
        }
    },
    actions: @Composable RowScope.() -> Unit = {}
) {
    when (variant) {
        YfyTopAppBarVariant.CENTER_ALIGNED -> {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = { navigationIcon?.invoke() },
                actions = actions,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = modifier.fillMaxWidth()
            )
        }
        YfyTopAppBarVariant.REGULAR -> {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = { navigationIcon?.invoke() },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun YfyTopAppBarCenterAlignedPreview() {
    YfyTopAppBar(
        title = stringResource(R.string.typography_title_large),
        variant = YfyTopAppBarVariant.CENTER_ALIGNED
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun YfyTopAppBarRegularPreview() {
    YfyTopAppBar(
        title = stringResource(R.string.typography_title_medium),
        variant = YfyTopAppBarVariant.REGULAR
    )
} 