package com.gamatechno.awor.global.model

import com.google.gson.annotations.SerializedName

data class ResponseAuth(
    @SerializedName("statusCode")
    var statusCode: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("token")
    var token: String? = "",
    @SerializedName("user")
    var user: User? = User()
)
