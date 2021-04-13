package com.gamatechno.awor.network

import android.content.Context
import com.gamatechno.awor.BuildConfig
import com.gamatechno.awor.utils.DateNullAdapterFactory
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkConfig {
    private var retrofit: Retrofit? = null
    private fun getRetrofit(context: Context): Retrofit? {
        if (retrofit == null) {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(DateNullAdapterFactory())
                .setLenient()
                .create()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.apply {
                loggingInterceptor.level =
                    if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
            }
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(NetworkConnectionInterceptor(context))
//            builder.addInterceptor(HeaderInterceptor(context))
            builder.addInterceptor(loggingInterceptor)
            builder.connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
            retrofit = Retrofit.Builder()
                .baseUrl(ApiConst.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build()
        }
        return retrofit
    }

    fun getAPIService(context: Context): ApiService? {
        return getRetrofit(context)?.create(ApiService::class.java)
    }
}