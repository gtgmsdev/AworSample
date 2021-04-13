package com.gamatechno.awor.jitsi.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class JoinUpdate : Serializable {
    @SerializedName("nameValuePairs")
    @Expose
    var nameValuePairs: NameValuePairs? = null
}