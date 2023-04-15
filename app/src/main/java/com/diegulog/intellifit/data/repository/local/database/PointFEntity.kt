package com.diegulog.intellifit.data.repository.local.database

import android.graphics.PointF
import io.realm.kotlin.types.RealmObject

class PointFEntity : RealmObject, DomainTranslatable<PointF> {
    var x: Float = 0f
    var y: Float = 0f
    override fun toDomain(): PointF {
        return PointF(x, y)
    }
    companion object{
        fun fromDomain(pointF: PointF): PointFEntity{
            return PointFEntity().apply {
                x = pointF.x
                y = pointF.y
            }
        }
    }
}