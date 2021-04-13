package com.gamatechno.awor.utils

object EmailValidUtil {
    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}