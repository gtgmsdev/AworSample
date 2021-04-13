package com.gamatechno.awor.home.model

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("picture")
    var picture: String? = ""
)
