package com.diegulog.intellifit.ui.home.training

import androidx.lifecycle.asLiveData
import com.diegulog.intellifit.domain.repository.TrainingRepository
import com.diegulog.intellifit.ui.base.BaseViewModel

class TrainingViewModel (
    private val trainingRepository: TrainingRepository
    ) : BaseViewModel()  {
        fun getTrainings() = trainingRepository.getTrainings().asLiveData()
    }