package com.diegulog.intellifit.ui.review

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegulog.intellifit.domain.entity.BodyPart
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import com.diegulog.intellifit.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoPreviewViewModel(
    private val context: Context,
    private val dataBaseRepository: DataBaseRepository
) : BaseViewModel() {

    fun getCaptures() = dataBaseRepository.getCaptures().asLiveData()

    fun deleteCapture(capture: Capture) = viewModelScope.launch(Dispatchers.IO) {
        dataBaseRepository.deleteCapture(capture)
        File(capture.videoPath).delete()
    }

    fun parseCsv(capture: Capture) = viewModelScope.launch(Dispatchers.IO) {
        val builder = StringBuilder()
        //nombres de las columnas
        builder.append(
            BodyPart.values().joinToString { "${it.name}_x,${it.name}_y,${it.name}_score" })
            .append(",class\n")
        capture.samples.forEach { person ->
            val inputVector = FloatArray(51)
            person.keyPoints.forEachIndexed { index, keyPoint ->
                inputVector[index * 3] = keyPoint.coordinate.x
                inputVector[index * 3 + 1] = keyPoint.coordinate.y
                inputVector[index * 3 + 2] = keyPoint.score
            }
            builder.append(inputVector.joinToString { value -> value.toString() }).append(",")
            builder.append(capture.moveType.ordinal).append("\n")
        }
        val nameFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ROOT)
        val file = File(context.filesDir.path, "${nameFormat.format(Date())}.csv")
        file.writeText(builder.toString())
        _info.send("csv guardado en ${file.absoluteFile}")
    }


}