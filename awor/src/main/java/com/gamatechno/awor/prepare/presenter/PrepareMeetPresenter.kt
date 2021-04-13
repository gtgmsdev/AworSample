package com.gamatechno.awor.prepare.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.prepare.view.PrepareMeetView
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class PrepareMeetPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<PrepareMeetView>() {

    fun prepareMeet(token: String, data: HashMap<String, Any?>) {
        getView()!!.showLoading()
        disposable!!.add(apiService.prepareRoom("Bearer $token", data)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya login ${it}")

                    if (it.statusCode == 201)
                        getView()!!.onSuccessPrepare(it)
                    else
                        getView()!!.onErrorPrepare(it.message!!)
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
                                500 -> getView()!!.onErrorPrepare(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorPrepare(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorPrepare(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorPrepare(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorPrepare(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorPrepare(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorPrepare(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorPrepare(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorPrepare(
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