package com.diegulog.intellifit.domain.entity

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Capture(
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("samples")
    val samples: List<Sample>,

    @SerializedName("videoPath")
    val videoPath: String,

    @SerializedName("moveType")
    var moveType: MoveType,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("exerciseId")
    var exerciseId: String
) : BaseEntity()
