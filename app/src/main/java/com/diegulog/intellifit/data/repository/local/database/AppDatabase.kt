package com.diegulog.intellifit.data.repository.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegulog.intellifit.data.repository.local.database.entity.*

@Database(entities = [CaptureEntity::class,  SampleEntity::class, ExerciseEntity::class, TrainingEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun captureDao(): CapturesDao
    abstract fun sampleDao(): SampleDao
    abstract fun trainingDao(): TrainingDao
    abstract fun exerciseDao(): ExerciseDao


}