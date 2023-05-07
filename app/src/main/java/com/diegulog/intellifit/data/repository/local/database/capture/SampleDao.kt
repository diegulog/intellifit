package com.diegulog.intellifit.data.repository.local.database.capture

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SampleDao{
    @Query("SELECT * FROM sample where captureId = :captureId ORDER BY timestamp")
    suspend fun getAllFromCapture(captureId:Long): List<SampleEntity>
    @Insert
    suspend fun save(entity: List<SampleEntity>)
}