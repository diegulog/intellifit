package com.diegulog.intellifit.domain.entity

import android.graphics.PointF
import com.google.gson.annotations.SerializedName

data class KeyPoint(
    @SerializedName("bodyPart")
    val bodyPart: BodyPart,
    @SerializedName("coordinate")
    var coordinate: PointF,
    @SerializedName("score")
    val score: Float
) : BaseEntity()
