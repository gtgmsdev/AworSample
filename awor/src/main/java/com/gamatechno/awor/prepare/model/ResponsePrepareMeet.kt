package com.gamatechno.awor.prepare.model

import com.gamatechno.awor.global.model.Meetroom
import com.google.gson.annotations.SerializedName

data class ResponsePrepareMeet(
    @SerializedName("statusCode")
    var statusCode: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("accessToken")
    var accessToken: String? = "",
    @SerializedName("userGroup")
    var userGroup: String? = "",
    @SerializedName("userName")
    var userName: String? = "",
    @SerializedName("userEmail")
    var userEmail: String? = "",
    @SerializedName("userAvatar")
    var userAvatar: String? = "",
    @SerializedName("meetroom")
    var meetroom: Meetroom? = Meetroom(),
    @SerializedName("sessionroom")
    var sessionRoom: SessionRoom? = SessionRoom(),
    @SerializedName("rtc")
    var rtc: Rtc? = Rtc()
)
