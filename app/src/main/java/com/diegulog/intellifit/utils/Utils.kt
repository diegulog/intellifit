package com.diegulog.intellifit.utils

import android.content.Context
import android.os.Build
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.diegulog.intellifit.BuildConfig
import com.diegulog.intellifit.domain.entity.BodyPart
import com.diegulog.intellifit.domain.entity.Capture
import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.path.Path
import kotlin.math.min
import kotlin.math.roundToInt


const val isDemo = BuildConfig.FLAVOR == "demo"

fun ImageView.load(path: String) {
    Glide.with(this.context).load(path).into(this)
}

fun <T> List<T>.reduceList(targetSize: Int): List<T> {
    if (targetSize < 2) {
        throw IllegalArgumentException("El tamaño objetivo debe estar entre 2 y el tamaño de la lista original")
    }

    if (targetSize == this.size) {
        return this // No se aplica reducción
    }
    //Queremos que la lista tenga siempre el primer y ultimo elemento
    val step = (this.size - 1).toDouble() / (targetSize - 1)
    return List(targetSize) { index ->
        val position = min((index * step).roundToInt(), this.lastIndex)
        this[position]
    }

}
fun buildModelIsEmulator(): Boolean {
    return  Build.MODEL.startsWith("sdk") ||
            "google_sdk" == Build.MODEL ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK")
}

fun Capture.parseCsv(context: Context) {
    val builder = StringBuilder()
    //nombres de las columnas
    builder.append(
        BodyPart.values().joinToString { "${it.name}_x,${it.name}_y,${it.name}_score" })
        .append(",class,class_name\n")
    this.samples.forEach { person ->
        val inputVector = FloatArray(51)
        person.keyPoints.forEachIndexed { index, keyPoint ->
            inputVector[index * 3] = keyPoint.coordinate.x
            inputVector[index * 3 + 1] = keyPoint.coordinate.y
            inputVector[index * 3 + 2] = keyPoint.score
        }
        builder.append(inputVector.joinToString { value -> value.toString() })
        builder.append(",${this.moveType.ordinal},${this.moveType.name}\n")
    }
    val nameFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS", Locale.ROOT)
    val file = File(
        context.filesDir.path + File.separator + this.modelId,
        "${this.moveType.name}-${nameFormat.format(Date())}.csv")

    file.parentFile?.let { Files.createDirectories(it.toPath()) }
    file.writeText(builder.toString())
}