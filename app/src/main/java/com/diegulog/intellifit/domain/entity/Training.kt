package com.diegulog.intellifit.domain.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class Training(
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("name")
    val name: String,

    @SerializedName("urlImage")
    val urlImage: String,

    @SerializedName("exercises")
    val exercises: List<Exercise>,

    @SerializedName("isPublic")
    val isPublic: Boolean,

    @SerializedName("duration")
    val duration: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("ownerId")
    val ownerId: String
) : BaseEntity()