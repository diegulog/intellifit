package com.diegulog.intellifit.domain.repository.remote

import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.domain.entity.User

interface NetworkDataSource {

    suspend fun login(username:String, password:String): String
    suspend fun signUp(user: User) : User
    suspend fun saveTraining(training: Training)
    suspend fun deleteTraining(id:String)
    suspend fun getTrainings(): List<Training>

    suspend fun saveExercise(exercise: Exercise, trainingId: String)
    suspend fun deleteExercise(id:String)
    suspend fun getExercises(trainingId: String): List<Exercise>

    suspend fun saveCapture(capture: Capture)
    suspend fun deleteCapture(id: String)
    suspend fun getCaptures(exerciseId: String): List<Capture>
}