package com.diegulog.intellifit.di

import androidx.room.Room
import com.diegulog.intellifit.data.repository.local.database.DataBaseRepositoryImpl
import com.diegulog.intellifit.data.repository.local.database.AppDatabase
import com.diegulog.intellifit.data.repository.local.database.capture.CapturesDao
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import com.diegulog.intellifit.utils.SoundPlayer
import org.koin.dsl.module


val appModuleTest = module {
    single { Room.inMemoryDatabaseBuilder(
        get(),
        AppDatabase::class.java)
        .build()
    }
    single<DataBaseRepository> { DataBaseRepositoryImpl(get()) }
}