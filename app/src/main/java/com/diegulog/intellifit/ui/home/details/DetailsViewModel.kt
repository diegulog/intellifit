package com.diegulog.intellifit.ui.home.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DetailsViewModel : BaseViewModel() {
    private var training: Training? = null
    var currentExerciseIndex = 0
    private val _start = MutableStateFlow(false)
    val start: LiveData<Boolean> = _start.asLiveData()

    fun getCurrentExercise(): Exercise {
        return training!!.exercises[currentExerciseIndex]
    }

    fun setStart(enable: Boolean){
        _start.value = enable
    }

    fun getNextExercise(): Exercise? {
        return training?.exercises?.getOrNull(++currentExerciseIndex)
    }

    fun setTraining(training: Training){
        this.training = training
        currentExerciseIndex = 0
        _start.value = false
    }
}
