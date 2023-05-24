package com.diegulog.intellifit.data.repository.remote

import com.diegulog.intellifit.domain.entity.*
import com.diegulog.intellifit.utils.isDemo
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("/api/v1/login/access-token")
    suspend fun login(@Field("username") username: String, @Field("password") password: String): Response<LoginResponse>

    @POST("signup")
    suspend fun signUp(@Body user: User): Response<User>

    @POST("/api/v1/training")
    suspend fun saveTraining(@Body training: Training): Response<Unit>

    @DELETE("/api/v1/training/{id}")
    suspend fun deleteTraining(@Path("id") id: String): Response<Unit>

    @GET("/api/v1/trainings")
    suspend fun getTrainings(): Response<List<Training>>

    @POST("/api/v1/exercise")
    suspend fun saveExercise(
        @Body exercise: Exercise,
        @Query("trainingId") trainingId: String
    ): Response<Unit>

    @DELETE("/api/v1/exercise/{id}")
    suspend fun deleteExercise(@Path("id") id: String): Response<Unit>

    @GET("/api/v1/exercises")
    suspend fun getExercises(@Query("trainingId") trainingId: String): Response<List<Exercise>>

    @POST("/api/v1/capture")
    suspend fun saveCapture(@Body capture: Capture): Response<Unit>

    @DELETE("/api/v1/capture/{id}")
    suspend fun deleteCapture(@Path("id") id: String): Response<Unit>

    @GET("/api/v1/captures")
    suspend fun getCaptures(): Response<List<Capture>>
}

fun createApiService(): ApiService {
    val client = OkHttpClient.Builder()

    if (isDemo)
        client.addInterceptor(ApiServiceMockInterceptor())

    val retrofit = Retrofit.Builder()
        .client(client.build())
        //TODO reemplazar con url backend
        .baseUrl("https://api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ApiService::class.java)

}