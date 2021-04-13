package com.gamatechno.awor.update.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.update.model.ResponseUpdateMeet

interface UpdateMeetView : BaseView {
    fun onSuccessUpdate(responseUpdateMeet: ResponseUpdateMeet)
    fun onErrorUpdate(msg: String)
}