package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.data.repository.local.database.capture.CaptureEntity
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import kotlinx.coroutines.flow.flow


class DataBaseRepositoryImpl(private val appDatabase: AppDatabase) : DataBaseRepository {

    override suspend fun saveCapture(capture: Capture){
        val captureEntity = CaptureEntity.fromDomain(capture)
        val idCapture = appDatabase.captureDao().save(captureEntity)
        capture.id = idCapture
        for (sample in captureEntity.samples) {
            sample.captureId = idCapture
        }
        appDatabase.sampleDao().save(captureEntity.samples)
    }

    override suspend fun deleteCapture(capture: Capture) {
        appDatabase.captureDao().delete(CaptureEntity.fromDomain(capture))
    }

    override fun getCaptures() = flow {
        emit(ResultOf.Loading)
        val captures = appDatabase.captureDao().getAll().map {
            it.samples = appDatabase.sampleDao().getAllFromCapture(it.id)
            it.toDomain()
        }
        emit(ResultOf.Success(captures))
    }

    override fun getCapture(id: Long) = flow {
        emit(ResultOf.Loading)
        val capture = appDatabase.captureDao().get(id)?.apply {
            this.samples = appDatabase.sampleDao().getAllFromCapture(this.id)
        }?.toDomain()
        emit(ResultOf.Success(capture))
    }
}