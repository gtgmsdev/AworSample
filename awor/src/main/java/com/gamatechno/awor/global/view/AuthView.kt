package com.gamatechno.awor.global.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.global.model.ResponseAuth

interface AuthView : BaseView {
    fun onSuccessAuth(responseAuth: ResponseAuth)
    fun onErrorAuth(msg: String)
}