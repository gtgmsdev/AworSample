package com.gamatechno.awor.create.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.create.model.ResponseCreateMeet

interface CreateMeetView : BaseView {
    fun onSuccessCreate(responseCreateMeet: ResponseCreateMeet)
    fun onErrorCreate(msg: String)
}