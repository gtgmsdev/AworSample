package com.gamatechno.awor.home.model

import com.google.gson.annotations.SerializedName

data class ResponseListMeet(
    @SerializedName("statusCode")
    var statusCode: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("total")
    var total: Int? = 0,
    @SerializedName("count")
    var count: Int? = 0,
    /*@SerializedName("ongoing")
    var ongoing: Int? = 0,*/
    @SerializedName("rows")
    var rows: List<Rows?>? = listOf()
)
