package com.gamatechno.awor.global.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("picture")
    var picture: String? = "",
    @SerializedName("services")
    var services: List<String?>? = listOf(),
    @SerializedName("role")
    var role: String? = "",
    @SerializedName("roleExpire")
    var roleExpire: String? = "",
    @SerializedName("language")
    var language: String? = "",
    @SerializedName("timezone")
    var timezone: String? = "",
    @SerializedName("createdAt")
    var createdAt: String? = ""
)
