package com.diegulog.intellifit.domain.repository

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {

    fun saveTraining(training: Training): Flow<ResultOf<Training>>
    fun deleteTraining(id: String): Flow<ResultOf<String>>
    fun getTrainings(): Flow<ResultOf<List<Training>>>
    fun saveExercise(exercise: Exercise, trainingId: String): Flow<ResultOf<Exercise>>
    fun deleteExercise(id: String): Flow<ResultOf<String>>
    fun getExercises(trainingId: String): Flow<ResultOf<List<Exercise>>>
    suspend fun saveCapture(capture: Capture)
    fun sendCapture(capture: Capture): Flow<ResultOf<Capture>>
    fun deleteCapture(id: String): Flow<ResultOf<String>>
    fun getCaptures(): Flow<ResultOf<List<Capture>>>

}