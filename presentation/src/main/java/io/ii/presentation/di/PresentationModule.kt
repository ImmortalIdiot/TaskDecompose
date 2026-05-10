package io.ii.presentation.di

import io.ii.presentation.core.ResourceProvider
import io.ii.presentation.core.ResourceProviderImpl
import io.ii.presentation.viewmodels.HistoryViewModel
import io.ii.presentation.viewmodels.TaskEditViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single<ResourceProvider> { ResourceProviderImpl(androidContext()) }

    viewModel { TaskEditViewModel(get(), get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get()) }
}
