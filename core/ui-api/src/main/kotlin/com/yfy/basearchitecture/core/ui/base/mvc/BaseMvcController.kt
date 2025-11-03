package com.yfy.basearchitecture.core.ui.base.mvc

abstract class BaseMvcController<M : BaseMvcModel>(
    protected val model: M
) {
    open fun onCleared() {
        model.onCleared()
    }
}
