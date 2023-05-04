package com.diegulog.intellifit.data.repository.local.database.capture

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CapturesDao{
    @Query("SELECT * FROM capture WHERE id = :id")
    fun get(id: Long): CaptureEntity?
    @Query("SELECT * FROM capture")
    fun getAll(): List<CaptureEntity>
    @Delete
    fun delete(entity: CaptureEntity)
    @Insert
    fun save(entity: CaptureEntity):Long
}