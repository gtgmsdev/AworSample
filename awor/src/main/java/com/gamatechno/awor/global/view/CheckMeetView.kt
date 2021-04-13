package com.gamatechno.awor.global.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.global.model.ResponseCheckMeet

interface CheckMeetView : BaseView {
    fun onSuccessCheck(responseCheckMeet: ResponseCheckMeet)
    fun onErrorCheck(msg: String)
}