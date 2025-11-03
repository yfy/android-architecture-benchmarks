package com.yfy.basearchitecture.core.ui.api.base

import kotlinx.coroutines.flow.Flow

abstract class BaseFlowUseCase<P, R> {

    operator fun invoke(parameters: P): Flow<R> = execute(parameters)

    protected abstract fun execute(parameters: P): Flow<R>
}