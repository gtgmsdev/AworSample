package com.gamatechno.awor.update.presenter

import android.util.Log
import com.gamatechno.awor.base.BasePresenter
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NoConnectionException
import com.gamatechno.awor.update.view.UpdateMeetView
import io.reactivex.Scheduler
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class UpdateMeetPresenter(
    var apiService: ApiService,
    var subscribeOn: Scheduler,
    var observeOn: Scheduler
) : BasePresenter<UpdateMeetView>() {

    fun update(token: String, id: String, data: HashMap<String, Any>) {
        getView()!!.showLoading()
        disposable!!.add(apiService.updateRoom(
            "Bearer $token",
            id,
            data)
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .subscribe({
                if (isViewAttached()) {
                    getView()!!.dismissLoading()

                    Log.d("KENALOG", "response nya $it")
                    

                    if (it.statusCode == 200)
                        getView()!!.onSuccessUpdate(it)
                    else
                        getView()!!.onErrorUpdate(it.message!!)
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
                                500 -> getView()!!.onErrorUpdate(
                                    msg = "Internal Server Error"
                                )
                                502 -> getView()!!.onErrorUpdate(
                                    msg = "Sistem dalam perbaikan"
                                )
                                503 -> getView()!!.onErrorUpdate(
                                    msg = "Sistem dalam perbaikan"
                                )
                                else -> {
                                    val responseBody =
                                        it.response().errorBody()
                                    try {
                                        val jsonObject = JSONObject(responseBody!!.string())

                                        val msgInfo = jsonObject.getString("message")
                                        getView()!!.onErrorUpdate(
                                            msg = msgInfo
                                        )
                                    } catch (e1: JSONException) {
                                        getView()!!.onErrorUpdate(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    } catch (e1: IOException) {
                                        getView()!!.onErrorUpdate(
                                            msg = it.message()
                                        )
                                        e1.printStackTrace()
                                    }
                                }
                            }
                        }
                        is NoConnectionException -> {
                            getView()!!.onErrorUpdate(
                                msg = it.localizedMessage!!
                            )
                        }
                        else -> {
                            if (it.localizedMessage != null || it.localizedMessage != "") {
                                getView()!!.onErrorUpdate(
                                    msg = it.localizedMessage!!
                                )
                            } else {
                                getView()!!.onErrorUpdate(
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