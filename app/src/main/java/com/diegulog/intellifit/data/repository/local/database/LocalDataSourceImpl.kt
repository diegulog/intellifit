package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.data.repository.local.database.entity.CaptureEntity
import com.diegulog.intellifit.data.repository.local.database.entity.ExerciseEntity
import com.diegulog.intellifit.data.repository.local.database.entity.TrainingEntity
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.domain.repository.local.LocalDataSource
import java.io.File

class LocalDataSourceImpl(private val appDatabase: AppDatabase) :
    LocalDataSource {

    override suspend fun saveCapture(capture: Capture) {
        val captureEntity = CaptureEntity.fromDomain(capture)
        appDatabase.captureDao().save(captureEntity)
        for (sample in captureEntity.samples) {
            sample.captureId = capture.id
        }
        appDatabase.sampleDao().save(captureEntity.samples)
    }

    override suspend fun deleteCapture(id: String) {
        appDatabase.captureDao().get(id)?.let {
            appDatabase.captureDao().delete(it.id)
            File(it.videoPath).delete()
        }
    }

    override suspend fun getCaptures(exerciseId: String): List<Capture> {
        val captures = appDatabase.captureDao().getAllFromExercise(exerciseId).map {
            it.samples = appDatabase.sampleDao().getAllFromCapture(it.id)
            it.toDomain()
        }
        return captures
    }

    override suspend fun saveTraining(training: Training) {
        val trainingEntity = TrainingEntity.fromDomain(training)
        appDatabase.trainingDao().delete(training.id)
        appDatabase.trainingDao().save(trainingEntity)
        for (exercise in trainingEntity.exercises) {
            exercise.trainingId = training.id
        }
        appDatabase.exerciseDao().save(trainingEntity.exercises)
    }

    override suspend fun deleteTraining(id: String) {
        appDatabase.trainingDao().delete(id)
    }

    override suspend fun getTrainings(): List<Training> {
        val trainings = appDatabase.trainingDao().getAll().map {
            it.exercises = appDatabase.exerciseDao().getAllFromTraining(it.id)
            it.toDomain()
        }
        return trainings
    }

    override suspend fun saveExercise(exercise: Exercise, trainingId: String) {
        val exerciseEntity = ExerciseEntity.fromDomain(exercise).apply {
            this.trainingId = trainingId
        }
        appDatabase.exerciseDao().save(exerciseEntity)
        for (capture in exerciseEntity.captures) {
            capture.exerciseId = exercise.id
        }
        appDatabase.captureDao().save(exerciseEntity.captures)
    }

    override suspend fun deleteExercise(id: String) {
        appDatabase.exerciseDao().delete(id)
    }

    override suspend fun getExercises(trainingId: String): List<Exercise> {
        val exercises = appDatabase.exerciseDao().getAllFromTraining(trainingId).map {
            it.captures = appDatabase.captureDao().getAllFromExercise(it.id)
            it.toDomain()
        }
        return exercises
    }

}