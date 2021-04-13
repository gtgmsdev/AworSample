package com.gamatechno.awor.jitsi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NameValuePairs_ : Serializable {
    @SerializedName("durationLimitParticipant")
    @Expose
    var durationLimitParticipant: Int? = null

    @SerializedName("durationLimitTime")
    @Expose
    var durationLimitTime: Int? = null
}