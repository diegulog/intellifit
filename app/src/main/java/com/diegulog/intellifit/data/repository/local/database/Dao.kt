package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.data.repository.local.database.capture.CaptureEntity


interface Dao<T> {

    suspend fun get(id: String): T?

    suspend fun getAll(): List<T>

    suspend fun save(entity: T): T

    suspend fun delete(entity: T)

}