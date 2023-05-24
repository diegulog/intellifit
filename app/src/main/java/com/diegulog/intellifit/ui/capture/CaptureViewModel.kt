package com.diegulog.intellifit.ui.capture

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegulog.intellifit.domain.entity.Capture
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
import kotlin.concurrent.timer
import kotlin.io.path.Path

class CaptureViewModel(
    private val idExercise: String,
    private val context: Context,
    private val soundPlayer: SoundPlayer,
    private val trainingRepository: TrainingRepository
) :
    BaseViewModel(), CameraSourceListener {

    interface VideoCaptureListener {
        fun onStartCapture(path: String)
        fun onStopCapture()
    }

    private val captures = mutableListOf<Capture>()
    private var timer: Timer? = null

    private val captureDuration = 2
    private val framesForSeconds = 6

    private val _isCapture = MutableStateFlow(false)
    val isCapture = _isCapture.asLiveData()

    private val _message = MutableStateFlow("0")
    val message = _message.asLiveData()

    private val sampleTemp = ConcurrentLinkedQueue<Sample>()

    private var videoCaptureListener: VideoCaptureListener? = null


    fun startCapture(duration: Int = captureDuration, videoCaptureListener: VideoCaptureListener) {
        this.videoCaptureListener = videoCaptureListener
        _isCapture.value = true
        capture(duration)
    }

    private fun capture(duration: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val prepareTime = max(duration, 3)
            var time = duration + prepareTime
            _message.value = "STOP"
            delay(2000)
            sampleTemp.clear()
            Files.createDirectories(Path("${context.filesDir.path}/videos/"))
            val nameFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ROOT)
            val filePath =
                File("${context.filesDir.path}/videos/", "${nameFormat.format(Date())}.mp4")
            timer = timer(period = 1000) {
                Timber.d("time: $time")
                when {
                    //Si quedan 3 segundos antes de empezar la captura
                    time <= duration + 3 && time > duration && _isCapture.value -> {
                        soundPlayer.playBeep()
                        _message.value = "${time - duration}"
                    }
                    //empezamos captura
                    time == duration && _isCapture.value -> {
                        videoCaptureListener?.onStartCapture(filePath.absolutePath)
                        _message.value = "START"
                        soundPlayer.playStart()
                    }
                    //finalizamos captura
                    time == 0 -> {
                        videoCaptureListener?.onStopCapture()
                        if (_isCapture.value) {
                            saveActualCapture(
                                duration,
                                filePath.absolutePath,
                                sampleTemp.map { it.copy() })
                            //Reiniciamos la captura
                            capture(duration)
                        }
                        this.cancel()
                    }
                }
                time -= 1
            }
        }
    }

    private fun saveActualCapture(duration: Int, filePath: String, sampleTemp: List<Sample>) =
        viewModelScope.launch(Dispatchers.IO) {
            if (sampleTemp.isEmpty()) {
                File(filePath).delete()
                _info.send("No se pudo guardar la captura, el tamaño de la muestra no es correcta")
                return@launch
            } // nada que guardar
            Timber.d("personTemp = ${sampleTemp.size}")

            val firstTime = sampleTemp.first().timestamp
            //que los tiempos inicien en 0
            sampleTemp.map { it.timestamp -= firstTime }

            val lastTime = sampleTemp.last().timestamp
            //Guardamos los primeros segundos de la captura como incorrect cuando la cuenta atras empieza
            val incorrect = sampleTemp.filter { it.timestamp <= duration * 1000 }

            //Guardamos los ultimos segundos de la captura como correcto
            val correct = sampleTemp.filter { it.timestamp > lastTime - duration * 1000 }
            Timber.d("correct = ${correct.size}")
            Timber.d("incorrect = ${incorrect.size}")
            //Comprobamos que el tamaño de la captura es igual a duration * framesForSeconds
            if (correct.size >= duration * framesForSeconds) {
                captures.add(
                    Capture(
                        samples = correct.reduceList(framesForSeconds * duration),
                        videoPath = filePath,
                        moveType = MoveType.CORRECT,
                        exerciseId = idExercise
                    ).apply {
                        samples.map {
                            it.captureId = this.id
                        }
                    }
                )
                if (incorrect.size >= duration * framesForSeconds) {
                    captures.add(
                        Capture(
                            samples = incorrect.reduceList(framesForSeconds * duration),
                            videoPath = "",
                            moveType = MoveType.INCORRECT,
                            exerciseId = idExercise
                        ).apply {
                            samples.map {
                                it.captureId = this.id
                            }
                        }
                    )
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
        captures.forEach {
            trainingRepository.saveCapture(it)
        }
        captures.clear()
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
            sampleTemp.add(sample)
        }
    }


}