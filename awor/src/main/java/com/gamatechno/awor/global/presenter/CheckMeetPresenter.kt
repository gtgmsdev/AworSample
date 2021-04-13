package com.gamatechno.awor.global.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.global.view.CheckMeetView
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class CheckMeetPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<CheckMeetView>() {

    fun check(token: String, data: HashMap<String, Any?>) {
        getView()!!.showLoading()
        disposable!!.add(apiService.checkRoom("Bearer $token", data)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya login ${it}")

                    if (it.statusCode == 200)
                        getView()!!.onSuccessCheck(it)
                    else
                        getView()!!.onErrorCheck(it.message!!)
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
                                500 -> getView()!!.onErrorCheck(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorCheck(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorCheck(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorCheck(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorCheck(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorCheck(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorCheck(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorCheck(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorCheck(
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