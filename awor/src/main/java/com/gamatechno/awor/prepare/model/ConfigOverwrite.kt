package com.gamatechno.awor.prepare.model

import com.google.gson.annotations.SerializedName

data class ConfigOverwrite(
    @SerializedName("channelLastN")
    var channelLastN: Double? = 0.0
)
