package com.gamatechno.awor.network

import com.gamatechno.awor.BuildConfig

object ApiConst {
    init {
        System.loadLibrary("keys")
    }

    private external fun getBaseLive(): String
    private external fun getBaseDev(): String

//    val BASE_URL = if (BuildConfig.DEBUG) "https://dev.awor.xyz/api/" else "https://dev.awor.xyz/api/"
    val BASE_URL = "${BuildConfig.base_api}api/"
}