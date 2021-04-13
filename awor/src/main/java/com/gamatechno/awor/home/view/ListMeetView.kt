package com.gamatechno.awor.home.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.home.model.ResponseListMeet

interface ListMeetView : BaseView {
    fun onSuccessList(responseListMeet: ResponseListMeet)
    fun onErrorList(msg: String)
}