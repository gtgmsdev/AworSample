package com.gamatechno.awor.create.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.create.view.CreateMeetView
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class CreateMeetPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<CreateMeetView>() {

    fun create(token: String, data: HashMap<String, Any>) {
        getView()!!.showLoading()
        disposable!!.add(apiService.createRoom(
            "awor-oid-eovizlite-6fcb8395",
            "Bearer $token",
            data)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya $it")

                    if (it.statusCode == 201)
                        getView()!!.onSuccessCreate(it)
                    else
                        getView()!!.onErrorCreate(it.message!!)
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
                                500 -> getView()!!.onErrorCreate(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorCreate(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorCreate(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorCreate(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorCreate(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorCreate(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorCreate(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorCreate(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorCreate(
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