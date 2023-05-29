package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CapturesDao{
    @Query("SELECT * FROM capture WHERE id = :id")
    suspend fun get(id: String): CaptureEntity?
    @Query("SELECT * FROM capture")
    suspend fun getAll(): List<CaptureEntity>
    @Query("DELETE FROM capture WHERE id = :id")
    suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: CaptureEntity)

    @Query("SELECT * FROM capture WHERE exerciseId = :exerciseId")
    suspend fun getAllFromExercise(exerciseId: String): List<CaptureEntity>
    @Insert
    suspend fun save(entities: List<CaptureEntity>)
}