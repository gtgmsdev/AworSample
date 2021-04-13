package com.gamatechno.awor.network

import com.gamatechno.awor.create.model.ResponseCreateMeet
import com.gamatechno.awor.global.model.ResponseAuth
import com.gamatechno.awor.global.model.ResponseCheckMeet
import com.gamatechno.awor.home.model.ResponseListMeet
import com.gamatechno.awor.home.model.ResponsePasswordMeet
import com.gamatechno.awor.prepare.model.ResponsePrepareMeet
import com.gamatechno.awor.update.model.ResponseUpdateMeet
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface ApiService {
    //<-----auth
    @FormUrlEncoded
    @POST("auth/eovizlite")
    fun auth(
        @Header("Client-ID") token: String,
        @FieldMap data: HashMap<String, Any>
    ): Observable<ResponseAuth>
    //-------------------->

    //<-----meetrooms
    @GET("meetrooms")
    fun getList(
        @Header("Authorization") token: String,
        @QueryMap data: HashMap<String, Any>
    ): Observable<ResponseListMeet>

    @FormUrlEncoded
    @POST("meetrooms")
    fun createRoom(
        @Header("Client-ID") clintId: String,
        @Header("Authorization") token: String,
        @FieldMap data: HashMap<String, Any>
    ): Observable<ResponseCreateMeet>

    @FormUrlEncoded
    @PUT("meetrooms/{id}")
    fun updateRoom(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @FieldMap data: HashMap<String, Any>
    ): Observable<ResponseUpdateMeet>

    @GET("meetrooms/pw/{code}")
    fun getPassword(
        @Header("Authorization") token: String,
        @Path("code") code: String
    ): Observable<ResponsePasswordMeet>

    @FormUrlEncoded
    @POST("participants")
    fun checkRoom(
        @Header("Authorization") token: String,
        @FieldMap data: HashMap<String, Any?>
    ): Observable<ResponseCheckMeet>

    @FormUrlEncoded
    @POST("participants")
    fun prepareRoom(
        @Header("Authorization") token: String,
        @FieldMap data: HashMap<String, Any?>
    ): Observable<ResponsePrepareMeet>
    //-------------------->
}