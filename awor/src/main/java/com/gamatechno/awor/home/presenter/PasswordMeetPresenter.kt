package com.gamatechno.awor.home.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.home.view.PasswordMeetView
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class PasswordMeetPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<PasswordMeetView>() {

    fun password(token: String, code: String) {
        getView()!!.showLoading()
        disposable!!.add(apiService.getPassword("Bearer $token", code)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya $it")

                    if (it.statusCode == 200)
                        getView()!!.onSuccessPassword(it)
                    else
                        getView()!!.onErrorPassword(it.message!!)
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
                                500 -> getView()!!.onErrorPassword(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorPassword(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorPassword(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorPassword(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorPassword(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorPassword(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorPassword(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorPassword(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorPassword(
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