package com.diegulog.intellifit.data.repository.local.database.capture

import android.graphics.PointF
import com.diegulog.intellifit.data.repository.local.database.DomainTranslatable
import com.diegulog.intellifit.domain.entity.BodyPart
import com.diegulog.intellifit.domain.entity.KeyPoint
import io.realm.kotlin.types.RealmObject

class KeyPointEntity : RealmObject, DomainTranslatable<KeyPoint> {
    private var bodyPartString: String = ""
    var bodyPart: BodyPart
        get() = BodyPart.valueOf(bodyPartString)
        set(value) {
            bodyPartString = value.name
        }
    var coordinate: PointFEntity = PointFEntity()
    var score: Float = 0f

    override fun toDomain(): KeyPoint {
        return KeyPoint(bodyPart, PointF(coordinate.x, coordinate.y), score)
    }

    companion object{
        fun fromDomain(keyPoint: KeyPoint): KeyPointEntity {
            return KeyPointEntity().apply {
                bodyPart = keyPoint.bodyPart
                coordinate = PointFEntity.fromDomain(keyPoint.coordinate)
                score = keyPoint.score
            }
        }
    }
}