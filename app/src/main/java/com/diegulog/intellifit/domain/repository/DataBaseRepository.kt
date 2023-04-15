package com.diegulog.intellifit.domain.repository

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.data.repository.local.database.CaptureEntity
import com.diegulog.intellifit.domain.entity.Capture
import kotlinx.coroutines.flow.Flow

interface DataBaseRepository {
    fun saveCapture(capture: Capture): Flow<ResultOf<Capture>>
    fun readCaptures(): Flow<ResultOf<List<Capture>>>
    fun readCapture(id: String): Flow<ResultOf<CaptureEntity?>>
}