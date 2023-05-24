package com.diegulog.intellifit.data.repository.remote

import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.domain.entity.User
import com.diegulog.intellifit.domain.repository.remote.ApiException
import com.diegulog.intellifit.domain.repository.remote.LoginException
import com.diegulog.intellifit.domain.repository.remote.NetworkDataSource
import com.diegulog.intellifit.domain.repository.remote.NetworkException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class NetworkDataSourceImpl(private val apiService: ApiService) : NetworkDataSource {

    override suspend fun login(username: String, password: String): String {
        try {
            val response = apiService.login(username, password)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                return body.accessToken
            } else if (response.code() == 401)
                //Si el codigo de respuesta es 400 el usuario o la contraseña sin incorrectas
                throw LoginException()
            else {
                throw ApiException("Error al comunicar con el servidor. Por favor intententalo mas tarde. Code: ${response.code()}")
            }
        } catch (ioe: IOException) {
            throw NetworkException()
        }
    }

    override suspend fun signUp(user: User): User {
        return networkCall { apiService.signUp(user)}!!
    }

    override suspend fun saveTraining(training: Training) {
        networkCall { apiService.saveTraining(training) }
    }

    override suspend fun deleteTraining(id: String) {
        networkCall { apiService.deleteTraining(id) }
    }

    override suspend fun getTrainings(): List<Training> {
        return networkCall { apiService.getTrainings() } ?: emptyList()
    }

    override suspend fun saveExercise(exercise: Exercise, trainingId: String) {
        networkCall { apiService.saveExercise(exercise, trainingId) }
    }

    override suspend fun deleteExercise(id: String) {
        networkCall { apiService.deleteExercise(id) }
    }

    override suspend fun getExercises(trainingId: String): List<Exercise> {
        return networkCall { apiService.getExercises(trainingId) } ?: emptyList()
    }

    override suspend fun saveCapture(capture: Capture) {
        networkCall { apiService.saveCapture(capture) }
    }

    override suspend fun deleteCapture(id: String) {
        networkCall { apiService.deleteCapture(id) }
    }

    override suspend fun getCaptures(): List<Capture> {
        return networkCall { apiService.getCaptures() } ?: emptyList()
    }

    private suspend fun <T> networkCall(block: suspend () -> Response<T>): T? {
        try {
            val response = block()
            if (response.isSuccessful) {
                return response.body()
            } else {
                response.errorBody()?.let {
                    val jsonObject = JSONObject(it.string())
                    val error = jsonObject.getString("detail")
                    throw ApiException(error)
                }
                throw ApiException("Error al comunicar con el servidor. Por favor intententalo mas tarde. Code: ${response.code()}")
            }
        } catch (ioe: IOException) {
            throw NetworkException()
        }
    }
}