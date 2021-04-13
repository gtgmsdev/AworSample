package com.gamatechno.awor.home.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.home.view.ListMeetView
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.HashMap

class ListMeetPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<ListMeetView>() {

    fun list(token: String, data : HashMap<String, Any>) {
        getView()!!.showLoading()
        disposable!!.add(apiService.getList("Bearer $token", data)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya $it")

                    if (it.statusCode == 200)
                        getView()!!.onSuccessList(it)
                    else
                        getView()!!.onErrorList(it.message!!)
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
                                500 -> getView()!!.onErrorList(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorList(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorList(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorList(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorList(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorList(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorList(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorList(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorList(
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