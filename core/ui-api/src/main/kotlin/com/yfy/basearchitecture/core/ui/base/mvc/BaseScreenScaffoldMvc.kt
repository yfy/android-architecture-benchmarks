package com.yfy.basearchitecture.core.ui.base.mvc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <C : BaseMvcController<*>> BaseScreenScaffoldMvc(
    controller: C,
    screenName: String,
    isLoading: Boolean = false,
    content: @Composable () -> Unit
) {
    DisposableEffect(screenName) {
        onDispose { }
    }

    DisposableEffect(controller) {
        onDispose {
            controller.onCleared()
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            content()

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}