package com.gamatechno.awor.base

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T : BaseView> {
    protected var disposable: CompositeDisposable? = null

    private var view: T? = null

    fun attachView(view: T) {
        this.view = view
        disposable = CompositeDisposable()
    }

    fun detachView() {
        view = null
        disposable!!.clear()
    }

    fun isViewAttached(): Boolean {
        return view != null
    }

    fun getView(): T? {
        return view
    }

    fun checkViewAttached() {
        if (!isViewAttached()) throw BaseViewNotAttachedException()
    }

    protected class BaseViewNotAttachedException internal constructor() :
        RuntimeException(
            "Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter"
        )

    fun getMessages(listMsg: List<String?>?) : String{
        var msgInfo = ""
        if (listMsg!=null) {
            if (listMsg.size == 1) {
                msgInfo = listMsg[0]!!
            } else {
                for (msg in listMsg) {
                    msgInfo += "&#8226; $msg<br/>"
                }
            }
        }
        return msgInfo
    }
}