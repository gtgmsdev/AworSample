package com.gamatechno.awor.prepare.model

import com.google.gson.annotations.SerializedName

data class SessionRoom(
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("code")
    var code: String? = "",
    @SerializedName("start")
    var start: String? = "",
    @SerializedName("end")
    var end: String? = "",
    @SerializedName("liveParticipants")
    var liveParticipants: List<LiveParticipants?>? = listOf(),
    @SerializedName("platform")
    var platform: String? = "",
    @SerializedName("createdAt")
    var createdAt: String? = "",
    @SerializedName("updatedAt")
    var updatedAt: String? = "",
    @SerializedName("rtcDomain")
    var rtcDomain: String? = ""
)
