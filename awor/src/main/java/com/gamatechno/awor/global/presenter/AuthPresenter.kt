package com.gamatechno.awor.global.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.global.view.AuthView
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AuthPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<AuthView>() {

    fun auth(data: HashMap<String, Any>) {
        getView()!!.showLoading()
        disposable!!.add(apiService.auth("awor-oid-eovizlite-6fcb8395", data)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya login ${it}")

                    if (it.statusCode == 201)
                        getView()!!.onSuccessAuth(it)
                    else
                        getView()!!.onErrorAuth(it.message!!)
                }
            }, {
                it.printStackTrace()

                Log.d("KENALOG", "error apa nya ${it.localizedMessage}")

                if (isViewAttached()) {
                    getView()!!.dismissLoading()
                    when (it) {
                        is HttpException -> {
                            when (it.code()) {
                                401 -> getView()!!.onUnAuthorize()
                                500 -> getView()!!.onErrorAuth(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorAuth(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorAuth(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorAuth(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorAuth(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorAuth(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorAuth(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorAuth(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorAuth(
                                    msg = "Unknown Error"
                                )
                            }
                        }
                    }
                }
            })
        )
    }
}