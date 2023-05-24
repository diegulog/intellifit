package com.diegulog.intellifit.ui.review

import android.content.Context
import androidx.lifecycle.asLiveData
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.repository.TrainingRepository
import com.diegulog.intellifit.ui.base.BaseViewModel

class VideoPreviewViewModel(
    private val context: Context,
    private val trainingRepository: TrainingRepository
) : BaseViewModel() {

    fun getCaptures() = trainingRepository.getCaptures().asLiveData()
    fun sendCapture(capture: Capture) = trainingRepository.sendCapture(capture).asLiveData()

    fun deleteCapture(capture: Capture) = trainingRepository.deleteCapture(capture.id).asLiveData()

}