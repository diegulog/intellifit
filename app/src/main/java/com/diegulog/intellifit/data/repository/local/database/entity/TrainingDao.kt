package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training WHERE id = :id")
    suspend fun get(id: String): TrainingEntity?

    @Query("SELECT * FROM training")
    suspend fun getAll(): List<TrainingEntity>

    @Query("DELETE FROM training WHERE id = :id")
    suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: TrainingEntity)
}
