package com.diegulog.intellifit.di

import com.diegulog.intellifit.data.repository.local.database.DataBaseRepositoryImpl
import com.diegulog.intellifit.data.repository.local.database.RealmDatabase
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import com.diegulog.intellifit.ui.capture.CaptureViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single { RealmDatabase().realm }
    single<DataBaseRepository> { DataBaseRepositoryImpl(get())}
    viewModel { CaptureViewModel(get()) }
}