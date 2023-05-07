package com.diegulog.intellifit.data.repository.local.database.capture

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CapturesDao{
    @Query("SELECT * FROM capture WHERE id = :id")
    suspend fun get(id: Long): CaptureEntity?
    @Query("SELECT * FROM capture")
    suspend fun getAll(): List<CaptureEntity>
    @Delete
    suspend fun delete(entity: CaptureEntity)
    @Insert
    suspend fun save(entity: CaptureEntity):Long
}