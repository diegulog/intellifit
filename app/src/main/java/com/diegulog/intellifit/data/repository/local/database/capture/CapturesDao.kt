package com.diegulog.intellifit.data.repository.local.database.capture

import com.diegulog.intellifit.data.repository.local.database.Dao
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class CapturesDao(private val realm: Realm) : Dao<CaptureEntity> {

    override suspend fun get(id: String): CaptureEntity? {
        return realm.query<CaptureEntity>("id == $0", id).first().find()
    }

    override suspend fun getAll(): List<CaptureEntity> {
        return realm.query<CaptureEntity>().find().toList()
    }

    override suspend fun delete(entity: CaptureEntity) {
        realm.write {
            this.delete(entity)
        }
    }
    override suspend fun save(entity: CaptureEntity): CaptureEntity {
        return realm.write {
            this.copyFromRealm(entity)
        }
    }
}