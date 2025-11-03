package com.yfy.basearchitecture.core.ui.api.extensions

import androidx.lifecycle.viewModelScope
import com.yfy.basearchitecture.core.ui.api.base.BaseComposeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun BaseComposeViewModel.serviceLaunch(
    scope: CoroutineScope = viewModelScope,
    block: suspend CoroutineScope.() -> Unit
):Job {
    return scope.launch(exceptionHandler, CoroutineStart.DEFAULT ) {
        block()
    }
}

