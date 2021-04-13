package com.gamatechno.awor.global.model

import com.google.gson.annotations.SerializedName

data class ResponseCheckMeet(
    @SerializedName("statusCode")
    var statusCode: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("meetroom")
    var meetroom: Meetroom? = Meetroom(),
    @SerializedName("accessCode")
    var accessCode: String? = ""
)
