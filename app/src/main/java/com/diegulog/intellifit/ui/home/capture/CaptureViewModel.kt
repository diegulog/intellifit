package com.diegulog.intellifit.ui.home.capture

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.MoveType
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.domain.repository.TrainingRepository
import com.diegulog.intellifit.movenet.camerax.CameraSourceListener
import com.diegulog.intellifit.ui.base.BaseViewModel
import com.diegulog.intellifit.utils.SoundPlayer
import com.diegulog.intellifit.utils.reduceList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.Integer.max
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.io.path.Path

class CaptureViewModel(
    private val exercise: Exercise,
    private val context: Context,
    private val soundPlayer: SoundPlayer,
    private val trainingRepository: TrainingRepository
) :
    BaseViewModel(), CameraSourceListener {

    private val nameFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS", Locale.ROOT)

    interface VideoCaptureListener {
        fun onStartCapture(path: String)
        fun onStopCapture()
    }

    private var timer: Timer? = null

    private val _isCapture = MutableStateFlow(false)
    val isCapture = _isCapture.asLiveData()

    private val _message = MutableStateFlow("0")
    val message = _message.asLiveData()

    private val incorrectTemp = ConcurrentLinkedQueue<Sample>()
    private val correctTemp = ConcurrentLinkedQueue<Sample>()

    private var videoCaptureListener: VideoCaptureListener? = null

    init {
        Files.createDirectories(Path("${context.filesDir.path}/videos/"))
    }


    fun startCapture(videoCaptureListener: VideoCaptureListener) {
        this.videoCaptureListener = videoCaptureListener
        _isCapture.value = true
        capture(exercise.duration)
    }

    private fun capture(duration: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val prepareTime = max(duration, 3)
            _message.value = "STOP"
            delay(1000)
            incorrectTemp.clear()
            val filePath =
                File("${context.filesDir.path}/videos/", "${nameFormat.format(Date())}.mp4")
            // Como tenemos que capturar movimientos negativos capturamos los instantes antes de empezar el ejercicio
            delay((prepareTime - 3) * 1000L)
            for (i in 3 downTo 1) {
                if (!_isCapture.value) return@launch
                playBeep()
                _message.value = "$i"
                delay(1000)
            }
            // esperamos unos milisegundos mas que el tiempo de reaccion entre el sonido y el movimiento puede no ser preciso
            if (!_isCapture.value) return@launch
            correctTemp.clear()
            videoCaptureListener?.onStartCapture(filePath.absolutePath)
            playStart()
            _message.value = "START"
            //empezamos a guardar el movimiento correcto
            delay((duration * 1000L) + 200L)

            if (!_isCapture.value) return@launch
            saveActualCapture(
                duration,
                filePath.absolutePath,
                correctTemp.map { it.copy() },
                incorrectTemp.map { it.copy() })
            videoCaptureListener?.onStopCapture()
            _message.value = "STOP"
            //Reiniciamos la captura
            capture(duration)
        }
    }

    private fun playBeep() = viewModelScope.launch(Dispatchers.IO) {
        soundPlayer.playBeep()
    }

    private fun playStart() = viewModelScope.launch(Dispatchers.IO) {
        soundPlayer.playStart()
    }

    private fun saveActualCapture(
        duration: Int,
        filePath: String,
        correctSample: List<Sample>,
        incorrectSamples: List<Sample>
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            if (correctSample.isEmpty()) {
                File(filePath).delete()
                _info.send("No se pudo guardar la captura, el tamaño de la muestra no es correcta")
                return@launch
            } // nada que guardar
            Timber.d("correctSamples = ${correctSample.size}")
            Timber.d("incorrectSamples = ${incorrectSamples.size}")
            //Guardamos los primeros segundos de la captura como incorrect cuando la cuenta atras empieza
            val incorrect = if (incorrectSamples.isNotEmpty()) {
                incorrectSamples.filter { it.timestamp <= incorrectSamples.first().timestamp + (duration * 1000) + 200}
            } else {
                emptyList()
            }
            //filtramos solo el tiempo extablecido para este ejercicio
            val correct =  correctSample.filter { it.timestamp <= correctSample.first().timestamp + (duration * 1000) + 200}
            Timber.d("correct = ${correctSample.size}")
            Timber.d("incorrect = ${incorrect.size}")
            //Comprobamos que el tamaño de la captura es igual a duration * framesForSeconds
            if (correct.size >= duration * SAMPLES_FOR_SECOND) {
                val correctCapture = Capture(
                    samples = correct.reduceList(SAMPLES_FOR_SECOND * duration),
                    videoPath = filePath,
                    moveType = MoveType.CORRECT,
                    exerciseId = exercise.id,
                    modelId = exercise.idModel
                ).apply {
                    samples.map {
                        it.id = UUID.randomUUID().toString()
                        it.captureId = this.id
                    }
                }
                trainingRepository.saveCapture(correctCapture)

                if (incorrect.size >= duration * SAMPLES_FOR_SECOND) {
                    val incorrectCapture = Capture(
                        samples = incorrect.reduceList(SAMPLES_FOR_SECOND * duration),
                        videoPath = "",
                        moveType = MoveType.INCORRECT,
                        exerciseId = exercise.id,
                        modelId = exercise.idModel
                    ).apply {
                        samples.map {
                            it.id = UUID.randomUUID().toString()
                            it.captureId = this.id
                        }
                    }
                    trainingRepository.saveCapture(incorrectCapture)
                }
            } else {
                Timber.d("Error al guardar captura correct.size = ${correct.size} , incorrect.size = ${incorrect.size}")
                _info.send("No se pudo guardar la captura, el tamaño de la muestra no es correcta")
            }

        }


    suspend fun stopCapture() {
        _isCapture.value = false
        _message.value = "STOP"
        timer?.cancel()
        videoCaptureListener?.onStopCapture()

    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }

    override fun onFPSListener(fps: Int) {
        Timber.d("fps %s", fps)
    }

    override fun onDetected(sample: Sample) {
        if (_isCapture.value) {
            incorrectTemp.add(sample)
            correctTemp.add(sample)
        }
    }


    companion object {
        const val SAMPLES_FOR_SECOND = 6

    }
}