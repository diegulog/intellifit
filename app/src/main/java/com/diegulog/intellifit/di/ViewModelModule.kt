package com.diegulog.intellifit.di

import com.diegulog.intellifit.ui.home.capture.CaptureViewModel
import com.diegulog.intellifit.ui.home.details.DetailsViewModel
import com.diegulog.intellifit.ui.home.exercise.ExerciseViewModel
import com.diegulog.intellifit.ui.home.training.TrainingViewModel
import com.diegulog.intellifit.ui.login.LoginViewModel
import com.diegulog.intellifit.ui.home.preview.VideoPreviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CaptureViewModel(get(), get(), get(), get()) }
    viewModel { VideoPreviewViewModel(get(), get(), get()) }
    viewModel { ExerciseViewModel( get(), get(), get()) }
    viewModel { LoginViewModel( get(), get(), get()) }
    viewModel { TrainingViewModel( get()) }
    viewModel { DetailsViewModel() }

}