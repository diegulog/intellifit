package com.diegulog.intellifit.ui.exercise

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.asLiveData
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.movenet.camerax.CameraSourceListener
import com.diegulog.intellifit.movenet.ml.ExerciseClassifier
import com.diegulog.intellifit.ui.base.BaseViewModel
import com.diegulog.intellifit.utils.SoundPlayer
import com.diegulog.intellifit.utils.reduceList
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.util.*

class ExerciseViewModel(
    private val context: Context,
    private val soundPlayer: SoundPlayer,
    private val exercise: Exercise
) : BaseViewModel() , CameraSourceListener  {
    private val sampleTemp = EvictingQueue(2000) // 2 segundos
    private val exerciseClassifier: ExerciseClassifier = ExerciseClassifier(exercise.idModel)
    private var exerciseStartTime: Long = -1
    private var lastSampleTime: Long = -1

    private val _outputInference = MutableStateFlow( floatArrayOf(0f,0f))
    val outputInference = _outputInference.asLiveData()

    private val _countReps = MutableStateFlow( 0)
    val countReps = _countReps.asLiveData()

    private var lastSampleTimestamp = 0

    override fun onFPSListener(fps: Int) {
    }

    override fun onDetected(sample: Sample) {
        sampleTemp.add(sample)
        val inferenceTimeMs: Long = System.currentTimeMillis() - lastSampleTime
        if(inferenceTimeMs > TIME_BETWEEN_INFERENCE){
            Timber.d("inference: %s  ", inferenceTimeMs)
            lastSampleTime = System.currentTimeMillis()
            _outputInference.value = exerciseClassifier.classify(sampleTemp.map { it.copy() }.reduceList(12))
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

    companion object{
        const val TIME_BETWEEN_INFERENCE = 200  // 500 mili
        private val RISE_THRESHOLD = 0.99f
        private const val MIN_EXERCISE_TIME_MS: Long = 800  // 800 mili


    }
}