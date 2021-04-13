package com.gamatechno.awor.prepare.view

import com.gamatechno.awor.base.BaseView
import com.gamatechno.awor.prepare.model.ResponsePrepareMeet

interface PrepareMeetView : BaseView {
    fun onSuccessPrepare(responsePrepareMeet: ResponsePrepareMeet)
    fun onErrorPrepare(msg: String)
}