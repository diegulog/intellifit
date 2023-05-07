package com.diegulog.intellifit.domain.repository

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.data.repository.local.database.capture.CaptureEntity
import com.diegulog.intellifit.domain.entity.Capture
import kotlinx.coroutines.flow.Flow

interface DataBaseRepository {
    suspend fun saveCapture(capture: Capture)
    suspend fun saveCapture(capture: List<Capture>)

    suspend fun deleteCapture(capture: Capture)

    fun getCaptures(): Flow<ResultOf<List<Capture>>>
    fun getCapture(id: Long): Flow<ResultOf<Capture?>>
}