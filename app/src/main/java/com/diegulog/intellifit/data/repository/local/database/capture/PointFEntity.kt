package com.diegulog.intellifit.data.repository.local.database.capture

import android.graphics.PointF
import com.diegulog.intellifit.data.repository.local.database.DomainTranslatable
import io.realm.kotlin.types.RealmObject

class PointFEntity : RealmObject, DomainTranslatable<PointF> {
    var x: Float = 0f
    var y: Float = 0f
    override fun toDomain(): PointF {
        return PointF(x, y)
    }
    companion object{
        fun fromDomain(pointF: PointF): PointFEntity {
            return PointFEntity().apply {
                x = pointF.x
                y = pointF.y
            }
        }
    }
}