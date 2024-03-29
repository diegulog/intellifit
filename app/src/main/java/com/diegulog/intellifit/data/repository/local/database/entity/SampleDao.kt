package com.diegulog.intellifit.data.repository.local.database.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SampleDao{
    @Query("SELECT * FROM sample where captureId = :captureId ORDER BY timestamp")
    suspend fun getAllFromCapture(captureId:String): List<SampleEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: List<SampleEntity>)
}