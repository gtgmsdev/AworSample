package com.gamatechno.awor.jitsi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class NameValuePairs : Serializable {
    @SerializedName("start")
    @Expose
    var start: Date? = null

    @SerializedName("participant")
    @Expose
    var participant: Int? = null

    @SerializedName("limit")
    @Expose
    var limit: Limit? = null
}