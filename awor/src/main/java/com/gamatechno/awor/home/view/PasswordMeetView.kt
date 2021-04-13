package com.gamatechno.awor.home.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.home.model.ResponsePasswordMeet

interface PasswordMeetView : BaseView {
    fun onSuccessPassword(responsePasswordMeet: ResponsePasswordMeet)
    fun onErrorPassword(msg: String)
}