package com.diegulog.intellifit.di

import com.diegulog.intellifit.data.repository.local.database.DataBaseRepositoryImpl
import com.diegulog.intellifit.data.repository.local.database.RealmDatabase
import com.diegulog.intellifit.data.repository.local.database.capture.CapturesDao
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import com.diegulog.intellifit.utils.SoundPlayer
import org.koin.dsl.module


val appModule = module {
    single { RealmDatabase().realm }
    factory { CapturesDao(get()) }
    single<DataBaseRepository> { DataBaseRepositoryImpl(get())}

    single{SoundPlayer(get())}
}