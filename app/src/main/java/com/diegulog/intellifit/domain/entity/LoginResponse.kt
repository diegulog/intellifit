package com.diegulog.intellifit.domain.entity

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    var accessToken: String
)