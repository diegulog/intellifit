package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise WHERE trainingId = :trainingId")
    suspend fun getAllFromTraining(trainingId: String): List<ExerciseEntity>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun get(id: String): ExerciseEntity?

    @Insert
    suspend fun save(entities: List<ExerciseEntity>)

    @Insert
    suspend fun save(entities: ExerciseEntity)

    @Query("DELETE FROM exercise WHERE id = :id")
    suspend fun delete(id: String)
}