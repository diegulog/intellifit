package com.diegulog.intellifit.di

import com.diegulog.intellifit.ui.capture.CaptureViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CaptureViewModel(get(), get()) }
}