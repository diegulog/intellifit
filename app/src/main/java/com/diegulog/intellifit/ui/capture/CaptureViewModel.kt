package com.diegulog.intellifit.ui.capture

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegulog.intellifit.domain.entity.BodyPart
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.MoveType
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import com.diegulog.intellifit.ui.base.BaseViewModel
import com.diegulog.intellifit.utils.SoundPlayer
import com.diegulog.intellifit.utils.reduceList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.lang.Integer.max
import java.lang.Thread.sleep
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.timer
import kotlin.io.path.Path

class CaptureViewModel(
    private val context: Context,
    private val soundPlayer: SoundPlayer,
    private val dataBaseRepository: DataBaseRepository
) :
    BaseViewModel() {

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
            sleep(1000)
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
                    time == 0  -> {
                        videoCaptureListener?.onStopCapture()
                        if(_isCapture.value){
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
            if (sampleTemp.isEmpty()) return@launch // nada que guardar
            Timber.d("personTemp = ${sampleTemp.size}")

            val firstTime = sampleTemp.first().timestamp
            //que los tiempos inicien en 0
            sampleTemp.map { it.timestamp -= firstTime }

            val lastTime = sampleTemp.last().timestamp
            //Guardamos los primeros segundos de la captura como incorrect cuando la cuenta atras empieza
            val incorrect =
                sampleTemp.filter { it.timestamp <= duration * 1000 }
                    .reduceList(framesForSeconds * duration)
            //Guardamos los ultimos segundos de la captura como correctp
            val correct = sampleTemp.filter { it.timestamp > lastTime - duration * 1000 }
                .reduceList(framesForSeconds * duration)

            Timber.d("incorrect = ${incorrect.size}")
            Timber.d("correct = ${correct.size}")
            //Comprobamos que el tamaño de la captura es igual a duration * framesForSeconds
            if (incorrect.size == duration * framesForSeconds &&
                correct.size == duration * framesForSeconds
            ) {
                captures.add(
                    Capture(
                        samples = incorrect,
                        videoPath = filePath,
                        moveType = MoveType.INCORRECT
                    )
                )
                captures.add(
                    Capture(
                        samples = correct,
                        videoPath = filePath,
                        moveType = MoveType.CORRECT
                    )
                )
            } else {
                Timber.d("Error al guardar captura correct.size = ${correct.size} , incorrect.size = ${incorrect.size}")
                _info.send("No se pudo guardar la captura, el tamaño de la muestra no es correcta")
            }

        }


    fun stopCapture(onSaved: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        _isCapture.value = false
        _message.value = "STOP"
        timer?.cancel()
        videoCaptureListener?.onStopCapture()
        //parseCsv()
        dataBaseRepository.saveCapture(captures)
        captures.clear()
        withContext(Dispatchers.Main) {
            onSaved()
        }

    }

    fun parseCsv() = viewModelScope.launch(Dispatchers.IO) {
        val builder = StringBuilder()
        //nombres de las columnas
        builder.append("id,").append(BodyPart.values().map {
            "${it.name}_x,${it.name}_y,${it.name}_score"
        }
            .joinToString()).append(",class\n")
        captures.forEachIndexed { index, capture ->
            capture.samples.forEach { person ->
                builder.append(index).append(",")
                val inputVector = FloatArray(51)
                person.keyPoints.forEachIndexed { index, keyPoint ->
                    inputVector[index * 3] = keyPoint.coordinate.x
                    inputVector[index * 3 + 1] = keyPoint.coordinate.y
                    inputVector[index * 3 + 2] = keyPoint.score
                }
                builder.append(inputVector.joinToString { value -> value.toString() }).append(",")
                builder.append(capture.moveType.ordinal).append("\n")
            }
        }
        val nameFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ROOT)
        val file = File(context.filesDir.path, "${nameFormat.format(Date())}.csv")
        file.writeText(builder.toString())
        _info.send("csv guardado en ${file.absoluteFile}")

    }

    fun addPerson(sample: Sample) {
        if (_isCapture.value) {
            sampleTemp.add(sample)
        }
    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }


}