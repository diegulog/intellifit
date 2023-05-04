package com.diegulog.intellifit.data.repository.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegulog.intellifit.data.repository.local.database.capture.*

@Database(entities = [CaptureEntity::class,  SampleEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun captureDao(): CapturesDao
    abstract fun sampleDao(): SampleDao
}