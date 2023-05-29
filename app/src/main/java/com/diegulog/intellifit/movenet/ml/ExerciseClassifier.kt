package com.diegulog.intellifit.movenet.ml

import com.diegulog.intellifit.domain.entity.Sample
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import timber.log.Timber

class ExerciseClassifier(idModel: String) {
    private var interpreter: Interpreter? = null

    companion object {
        private const val CPU_NUM_THREADS = 4
    }

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(CPU_NUM_THREADS)
        }
        val conditions = CustomModelDownloadConditions.Builder()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(idModel, DownloadType.LATEST_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                model.file?.let {
                    interpreter = Interpreter(it, options)
                }

            }.addOnFailureListener { t: Exception? -> Timber.e(t) }

    }

    fun classify(samples: List<Sample>): FloatArray {
        if (interpreter != null) {
            val input = interpreter!!.getInputTensor(0).shape()
            val output = interpreter!!.getOutputTensor(0).shape()
            // Preprocess the pose estimation result to a flat array
            val inputVector = Array(input[1]) { FloatArray(input[2]) }
            samples.forEachIndexed { i, sample ->
                sample.keyPoints.forEachIndexed { index, keyPoint ->
                    inputVector[i][index * 3] = keyPoint.coordinate.x
                    inputVector[i][index * 3 + 1] = keyPoint.coordinate.y
                    inputVector[i][index * 3 + 2] = keyPoint.score
                }
            }
            // Postprocess the model output to human readable class names
            val outputTensor = FloatArray(output[1])
            interpreter?.run(arrayOf(inputVector), arrayOf(outputTensor))
            return outputTensor
        }
        return floatArrayOf(0f,0f)
    }

    fun close() {
        interpreter?.close()
    }
}
