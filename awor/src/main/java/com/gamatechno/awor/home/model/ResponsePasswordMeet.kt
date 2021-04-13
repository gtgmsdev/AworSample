package com.gamatechno.awor.home.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ResponsePasswordMeet(
    @SerializedName("statusCode")
    var statusCode: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("owner")
    var owner: Owner? = Owner(),
    @SerializedName("moderator")
    var moderator: List<Owner?>? = listOf(),
    @SerializedName("code")
    var code: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("anonymous")
    var anonymous: Boolean? = false,
    @SerializedName("password")
    var password: String? = "",
    @SerializedName("start")
    var start: Date? = null,
    @SerializedName("end")
    var end: Date? = null,
    @SerializedName("reusable")
    var reusable: Boolean? = false,
    @SerializedName("expired")
    var expired: String? = "",
    @SerializedName("tags")
    var tags: List<String?>? = listOf(),
    @SerializedName("createdAt")
    var createdAt: Date? = null,
    @SerializedName("updatedAt")
    var updatedAt: Date? = null,
    @SerializedName("ongoing")
    var ongoing: Int? = 0,
    @SerializedName("whitelistMode")
    var whitelistMode: Boolean? = false,
    @SerializedName("extras")
    var extras: Extras? = Extras()
)