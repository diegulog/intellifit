package com.diegulog.intellifit.ui.home.exercise

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.movenet.camerax.CameraSourceListener
import com.diegulog.intellifit.movenet.ml.ExerciseClassifier
import com.diegulog.intellifit.ui.base.BaseViewModel
import com.diegulog.intellifit.ui.home.capture.CaptureViewModel.Companion.SAMPLES_FOR_SECOND
import com.diegulog.intellifit.utils.SoundPlayer
import com.diegulog.intellifit.utils.reduceList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ExerciseViewModel(
    private val context: Context,
    private val soundPlayer: SoundPlayer,
    private val exercise: Exercise
) : BaseViewModel(), CameraSourceListener {
    private val sampleTemp = EvictingQueue((exercise.duration * 1000L) + 100)
    private val exerciseClassifier: ExerciseClassifier = ExerciseClassifier(exercise.idModel)
    private var exerciseStartTime: Long = -1
    private var lastSampleTime: Long = -1

    private val _outputInference = MutableStateFlow(floatArrayOf(0f, 0f))
    val outputInference = _outputInference.asLiveData()

    private val _countReps = MutableStateFlow(0)
    val countReps = _countReps.asLiveData()

    private var lastSampleTimestamp = 0

    override fun onFPSListener(fps: Int) {
    }

    override fun onDetected(sample: Sample) {
        sampleTemp.add(sample)
        val inferenceTimeMs: Long = System.currentTimeMillis() - lastSampleTime
        if (inferenceTimeMs > TIME_BETWEEN_INFERENCE) {
            Timber.d("inference: %s  ", inferenceTimeMs)
            lastSampleTime = System.currentTimeMillis()
            _outputInference.value = exerciseClassifier.classify(sampleTemp.map { it.copy() }
                .reduceList(SAMPLES_FOR_SECOND * exercise.duration))
            detectCorrectExercise(_outputInference.value[0])
        }

    }

    private fun detectCorrectExercise(output: Float) {
        if (output >= RISE_THRESHOLD) {
            val exerciseTimeMs: Long =
                (System.currentTimeMillis() - exerciseStartTime)
            if (exerciseTimeMs > exercise.duration * MIN_EXERCISE_TIME_MS) {
                Timber.d("MIN_EXERCISE_TIME_MS: %s  ", exercise.duration * MIN_EXERCISE_TIME_MS)
                Timber.d("detectCorrectExercise: %s  ", exerciseTimeMs)
                exerciseStartTime = System.currentTimeMillis()
                _countReps.value = _countReps.value + 1
            }
        }
    }

    override fun onCleared() {
        exerciseClassifier.close()
        super.onCleared()
    }

    fun playBeep() = viewModelScope.launch(Dispatchers.IO) {
        soundPlayer.playBeep()
    }

    fun playStart() = viewModelScope.launch(Dispatchers.IO) {
        soundPlayer.playStart()
    }

    companion object {
        const val TIME_BETWEEN_INFERENCE = 300  // 300 mili
        private val RISE_THRESHOLD = 0.98f
        private const val MIN_EXERCISE_TIME_MS: Long = 500  // 800 mili


    }
}