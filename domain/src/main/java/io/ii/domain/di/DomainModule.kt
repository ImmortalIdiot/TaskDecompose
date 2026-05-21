package io.ii.domain.di

import io.ii.domain.usecase.DecomposeTaskUseCase
import io.ii.domain.usecase.DeleteTaskUseCase
import io.ii.domain.usecase.GetTaskUseCase
import io.ii.domain.usecase.LoadDecompositionHistoryUseCase
import io.ii.domain.usecase.SaveLlmSettingsUseCase
import io.ii.domain.usecase.UpdateTaskUseCase
import org.koin.dsl.module

val domainModule = module {
    single { DecomposeTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { LoadDecompositionHistoryUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { SaveLlmSettingsUseCase(get()) }
}
