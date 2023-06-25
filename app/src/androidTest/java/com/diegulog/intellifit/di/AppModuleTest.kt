package com.diegulog.intellifit.di

import androidx.room.Room
import com.diegulog.intellifit.data.repository.local.database.LocalDataSourceImpl
import com.diegulog.intellifit.data.repository.local.database.AppDatabase
import com.diegulog.intellifit.domain.repository.local.LocalDataSource
import org.koin.dsl.module


val appModuleTest = module {
    single { Room.inMemoryDatabaseBuilder(
        get(),
        AppDatabase::class.java)
        .build()
    }
    single<LocalDataSource> { LocalDataSourceImpl(get()) }
}