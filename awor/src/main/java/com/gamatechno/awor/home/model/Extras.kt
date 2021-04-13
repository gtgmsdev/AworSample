package com.gamatechno.awor.home.model

import com.google.gson.annotations.SerializedName

data class Extras(
    @SerializedName("gCalEventId")
    var gCalEventId: String? = ""
)
