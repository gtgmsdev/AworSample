package com.gamatechno.awor.utils

import android.text.TextUtils
import android.view.View
import android.widget.EditText

object Validate {
    fun cek(et: EditText): Boolean {
        var focusView: View? = null
        var cancel = false
        if (TextUtils.isEmpty(et.text.toString().trim { it <= ' ' })) {
            et.error = "This field is required"
            focusView = et
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()
        }
        return cancel
    }

    fun cekString(et: String?): Boolean {
        var cancel = false
        if (TextUtils.isEmpty(et)) {
            cancel = true
        }
        return cancel
    }

    fun cekPassword(et: EditText, Password: String, ConfirmPassword: String): Boolean {
        var focusView: View? = null
        var cancel = false
        if (Password != ConfirmPassword) {
            et.error = "Password not match"
            focusView = et
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()
        }
        return cancel
    }
}