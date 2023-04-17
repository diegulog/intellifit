package com.diegulog.intellifit.data.repository.local.database.capture

import com.diegulog.intellifit.data.repository.local.database.DomainTranslatable
import com.diegulog.intellifit.data.repository.local.database.toRealmList
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.MoveType
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*

class CaptureEntity : RealmObject, DomainTranslatable<Capture> {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var persons: RealmList<PersonEntity> = realmListOf()
    var videoPath: String = ""
    private var moveTypeString: String = MoveType.INCORRECT.name
    var moveType: MoveType
        get() = MoveType.valueOf(moveTypeString)
        set(value) {
            moveTypeString = value.name
        }
    var timestamp: Long = 0

    override fun toDomain(): Capture {
        return Capture(
            id = id,
            persons = persons.map { it.toDomain() },
            videoPath = videoPath,
            moveType = moveType,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomain(capture: Capture): CaptureEntity {
            return CaptureEntity().apply {
                id = capture.id
                persons = capture.persons.map { PersonEntity.fromDomain(it) }.toRealmList()
                videoPath = capture.videoPath
                moveType = capture.moveType
                timestamp = capture.timestamp

            }
        }
    }

}

