package com.diegulog.intellifit.domain.repository.local

import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training

interface LocalDataSource {
    suspend fun saveTraining(training: Training)
    suspend fun deleteTraining(id:String)
    suspend fun getTrainings(): List<Training>

    suspend fun saveExercise(exercise: Exercise, trainingId: String)
    suspend fun deleteExercise(id:String)
    suspend fun getExercises(trainingId: String): List<Exercise>

    suspend fun saveCapture(capture: Capture)
    suspend fun deleteCapture(id: String)
    suspend fun getCaptures(): List<Capture>
}