package com.diegulog.intellifit.domain.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("height")
    val height: Float?=null,
    @SerializedName("weight")
    val weight: Float?=null
) : BaseEntity()