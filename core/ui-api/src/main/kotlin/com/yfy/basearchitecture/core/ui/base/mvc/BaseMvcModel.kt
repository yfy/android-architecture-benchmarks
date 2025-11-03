package com.yfy.basearchitecture.core.ui.base.mvc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseMvcModel {
    protected val modelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    open fun onCleared() {
        modelScope.cancel()
    }
}
