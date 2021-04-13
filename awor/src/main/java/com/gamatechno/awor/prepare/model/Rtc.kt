package com.gamatechno.awor.prepare.model

import com.google.gson.annotations.SerializedName

data class Rtc(
    @SerializedName("platform")
    var platform: String? = "",
    @SerializedName("domain")
    var domain: String? = "",
    @SerializedName("extraTools")
    var extraTools: List<String?>? = listOf(),
    @SerializedName("configOverwrite")
    var configOverwrite: ConfigOverwrite? = ConfigOverwrite()
)
