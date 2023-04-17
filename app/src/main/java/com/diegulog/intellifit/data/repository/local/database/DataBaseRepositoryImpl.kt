package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.data.repository.local.database.capture.CaptureEntity
import com.diegulog.intellifit.data.repository.local.database.capture.CapturesDao
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import kotlinx.coroutines.flow.flow


class DataBaseRepositoryImpl(private val capturesDao: CapturesDao) : DataBaseRepository {

    override fun saveCapture(capture: Capture) = flow {
        emit(ResultOf.Loading)
        val result = capturesDao.save(CaptureEntity.fromDomain(capture))
        emit(ResultOf.Success(result.toDomain()))
    }

    override fun getCaptures() = flow {
        emit(ResultOf.Loading)
        val captures =  capturesDao.getAll().map { it.toDomain() }
        emit(ResultOf.Success(captures))
    }

    override fun getCapture(id:String) = flow {
        emit(ResultOf.Loading)
        val capture = capturesDao.get(id)?.toDomain()
        emit(ResultOf.Success(capture))
    }
}