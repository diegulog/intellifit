package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.data.repository.local.database.capture.PersonEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList


class RealmDatabase{
    private val schema = setOf(PersonEntity::class)
    private val configuration = RealmConfiguration.Builder(schema)
        .schemaVersion(1)
        .deleteRealmIfMigrationNeeded()
        .name("Intellifit")
        .build()
    val realm = Realm.open(configuration)
}

fun <T> List<T>.toRealmList(): RealmList<T>{
    val reamList = realmListOf<T>()
    reamList.addAll(this)
    return reamList
}