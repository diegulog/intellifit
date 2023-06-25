package com.diegulog.intellifit.di

import androidx.room.Room
import com.diegulog.intellifit.data.repository.TrainingRepositoryImpl
import com.diegulog.intellifit.data.repository.local.database.LocalDataSourceImpl
import com.diegulog.intellifit.data.repository.local.database.AppDatabase
import com.diegulog.intellifit.data.repository.local.preferences.AppPreferencesImpl
import com.diegulog.intellifit.data.repository.remote.ApiService
import com.diegulog.intellifit.data.repository.remote.NetworkDataSourceImpl
import com.diegulog.intellifit.data.repository.UserRepositoryImpl
import com.diegulog.intellifit.data.repository.remote.createApiService
import com.diegulog.intellifit.domain.repository.TrainingRepository
import com.diegulog.intellifit.domain.repository.UserRepository
import com.diegulog.intellifit.domain.repository.local.AppPreferences
import com.diegulog.intellifit.domain.repository.local.LocalDataSource
import com.diegulog.intellifit.domain.repository.remote.NetworkDataSource
import com.diegulog.intellifit.utils.SoundPlayer
import org.koin.dsl.module


val appModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "intellifit"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single<ApiService> {
        createApiService()
    }
    single<LocalDataSource> { LocalDataSourceImpl(get()) }
    single<NetworkDataSource> { NetworkDataSourceImpl(get()) }

    single<TrainingRepository> { TrainingRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    single<AppPreferences> { AppPreferencesImpl(get()) }
    single { SoundPlayer(get()) }
}