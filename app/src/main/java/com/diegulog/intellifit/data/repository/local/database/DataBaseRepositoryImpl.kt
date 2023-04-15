package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.Person
import com.diegulog.intellifit.domain.repository.DataBaseRepository
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ObjectChange
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow


class DataBaseRepositoryImpl : DataBaseRepository {
    private val realmDatabase by lazy { RealmDatabase() }

    override fun saveCapture(capture: Capture) = flow {
        emit(ResultOf.Loading)
        val result = realmDatabase.get().write {
            this.copyFromRealm(
                CaptureEntity.fromDomain(capture)
            )
        }
        emit(ResultOf.Success(result.toDomain()))
    }

    override fun readCaptures() = flow {
        emit(ResultOf.Loading)
        val captures = realmDatabase.get().query<CaptureEntity>().find().map { it.toDomain() }
        emit(ResultOf.Success(captures))
    }

    override fun readCapture(id:String) = flow {
        emit(ResultOf.Loading)
        val capture = realmDatabase.get().query<CaptureEntity>("id == $0", id).first().find()
        emit(ResultOf.Success(capture))
    }
}