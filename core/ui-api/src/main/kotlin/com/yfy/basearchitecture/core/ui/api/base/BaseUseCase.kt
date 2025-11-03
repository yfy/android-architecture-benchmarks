package com.yfy.basearchitecture.core.ui.api.base

abstract class BaseUseCase<P, R> {

    operator fun invoke(parameters: P): R = execute(parameters)

    protected abstract fun execute(parameters: P): R
}