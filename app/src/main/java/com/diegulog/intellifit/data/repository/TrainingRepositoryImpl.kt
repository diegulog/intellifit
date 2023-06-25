package com.diegulog.intellifit.data.repository

import android.content.Context
import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.domain.repository.TrainingRepository
import com.diegulog.intellifit.domain.repository.local.LocalDataSource
import com.diegulog.intellifit.domain.repository.remote.NetworkDataSource
import com.diegulog.intellifit.utils.isDemo
import com.diegulog.intellifit.utils.parseCsv
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class TrainingRepositoryImpl(
    private val context: Context,
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : TrainingRepository {

    override fun saveTraining(training: Training) = callFlow {
        networkDataSource.saveTraining(training)
        localDataSource.saveTraining(training)
        emit(ResultOf.Success(training))
    }

    override fun deleteTraining(id: String) = callFlow {
        networkDataSource.deleteTraining(id)
        localDataSource.deleteTraining(id)
        emit(ResultOf.Success(id))
    }

    override fun getTrainings() = callFlow {
        val localTrainings = localDataSource.getTrainings()
        if(localTrainings.isNotEmpty())
            emit(ResultOf.Success(localTrainings))
        val networkTrainings = networkDataSource.getTrainings()
        emit(ResultOf.Success(networkTrainings))
        networkTrainings.forEach {
            localDataSource.saveTraining(it)
        }

    }

    override fun saveExercise(exercise: Exercise, trainingId: String) = callFlow {
        networkDataSource.saveExercise(exercise, trainingId)
        localDataSource.saveExercise(exercise, trainingId)
        emit(ResultOf.Success(exercise))
    }

    override fun deleteExercise(id: String) = callFlow {
        networkDataSource.deleteExercise(id)
        localDataSource.deleteExercise(id)
        emit(ResultOf.Success(id))
    }

    override fun getExercises(trainingId: String) = callFlow {
        val localExercises = localDataSource.getExercises(trainingId)
        emit(ResultOf.Success(localExercises))

        val networkExercises = networkDataSource.getExercises(trainingId)
        emit(ResultOf.Success(networkExercises))
        networkExercises.forEach {
            localDataSource.saveExercise(it, trainingId)
        }
    }

    override suspend fun saveCapture(capture: Capture) {
        localDataSource.saveCapture(capture)
    }

    override fun sendCapture(capture: Capture) = callFlow {
        networkDataSource.saveCapture(capture)
        if (isDemo)
            capture.parseCsv(context)
        emit(ResultOf.Success(capture))
    }

    override suspend fun deleteCapture(id: String) {
        localDataSource.deleteCapture(id)
    }


    override fun getCaptures(exerciseId: String) = callFlow {
        val localCaptures = localDataSource.getCaptures(exerciseId)
        emit(ResultOf.Success(localCaptures))

     /*   val networkCaptures = networkDataSource.getCaptures(exerciseId)
        emit(ResultOf.Success(networkCaptures))

        networkCaptures.forEach {
            localDataSource.saveCapture(it)
        }
        */
    }

    private fun <T> callFlow(block: suspend FlowCollector<ResultOf<T>>.() -> Unit) = flow {
        emit(ResultOf.Loading)
        try {
            block.invoke(this)
        } catch (e: Exception) {
            Timber.e(e)
            emit(ResultOf.Failure(e))
        }
    }

}