package com.gamatechno.awor.network

import android.content.Context
import com.gamatechno.awor.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(context: Context) : Interceptor {
    private var sessionManager: SessionManager? = SessionManager(context)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Client-ID", "awor-oid-eovizlite-6fcb8395")
            .build()
        return chain.proceed(request)
    }
}