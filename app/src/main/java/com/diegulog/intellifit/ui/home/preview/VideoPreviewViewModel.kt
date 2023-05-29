package com.diegulog.intellifit.ui.home.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.view.Surface
import android.view.SurfaceView
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.domain.repository.TrainingRepository
import com.diegulog.intellifit.movenet.camerax.CameraXFragment
import com.diegulog.intellifit.ui.base.BaseViewModel
import com.diegulog.intellifit.utils.VisualizationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class VideoPreviewViewModel(
    private val context: Context,
    private val trainingRepository: TrainingRepository,
    private val exercise: Exercise
    ) : BaseViewModel() {
    fun getCaptures() = trainingRepository.getCaptures(exercise.id).asLiveData()
    fun sendCapture(capture: Capture) = trainingRepository.sendCapture(capture).asLiveData()


    fun deleteCapture(capture: Capture) =  viewModelScope.launch(Dispatchers.IO) {
        trainingRepository.deleteCapture(capture.id)
    }

    fun playCapture(surfaceView: SurfaceView, capture: Capture) = liveData {
        emit(ResultOf.Loading)
        capture.samples.forEach { sample ->
            visualize(surfaceView, sample)
            delay(167)
        }
        emit(ResultOf.Success(Unit))
    }


    private fun visualize(surfaceView: SurfaceView, sample: Sample) {
        val outputBitmap = VisualizationUtils.drawBodyKeypoints(listOf(sample))
        surfaceView.setZOrderOnTop(true)
        val holder = surfaceView.holder
        holder.setFormat(PixelFormat.TRANSPARENT)

        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)

            if (canvas.height > canvas.width) {
                val ratio = outputBitmap.height.toFloat() / outputBitmap.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = outputBitmap.width.toFloat() / outputBitmap.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                outputBitmap, Rect(0, -100, outputBitmap.width, outputBitmap.height),
                Rect(left, top, right, bottom), null
            )
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }

}