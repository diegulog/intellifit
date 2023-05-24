package com.diegulog.intellifit.domain.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class Sample(
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("keyPoints")
    val keyPoints: List<KeyPoint>,

    @SerializedName("score")
    val score: Float,

    @SerializedName("timestamp")
    var timestamp: Long = System.currentTimeMillis(),

    @SerializedName("captureId")
    var captureId: String = ""
):BaseEntity()
