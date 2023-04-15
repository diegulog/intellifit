package com.diegulog.intellifit.data.repository.local.database

import com.diegulog.intellifit.domain.entity.Person
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class PersonEntity : RealmObject, DomainTranslatable<Person> {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var keyPoints: RealmList<KeyPointEntity> = realmListOf()
    var score: Float = 0f
    var timestamp: Long = System.currentTimeMillis()


    override fun toDomain(): Person {
        return Person(id = id, keyPoints = keyPoints.map { it.toDomain()}, score = score, timestamp = timestamp)
    }
    companion object{
        fun fromDomain(person: Person): PersonEntity{
            return PersonEntity().apply {
                id = person.id
                keyPoints = person.keyPoints.map { KeyPointEntity.fromDomain(it) }.toRealmList()
                score = person.score
                timestamp = person.timestamp
            }
        }
    }

}