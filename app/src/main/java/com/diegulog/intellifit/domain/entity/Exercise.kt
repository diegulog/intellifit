package com.diegulog.intellifit.domain.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class Exercise(
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("name")
    val name: String,

    @SerializedName("urlImage")
    val urlImage: String,

    @SerializedName("urlVideo")
    val urlVideo: String,

    @SerializedName("duration")
    val duration: Int,

    @SerializedName("idModel")
    val idModel: String,

    @SerializedName("captures")
    val captures: List<Capture>,

    @SerializedName("isPublic")
    val isPublic: Boolean,

    @SerializedName("description")
    val description: String,

    @SerializedName("trainingId")
    var trainingId: String,

    @SerializedName("repeat")
    var repeat: Int
): BaseEntity()
