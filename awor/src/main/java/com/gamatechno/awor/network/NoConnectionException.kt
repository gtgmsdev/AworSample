package com.gamatechno.awor.network

import android.content.Context
import java.io.IOException

class NoConnectionException(var context: Context) : IOException() {
    override fun getLocalizedMessage(): String {
        return "Tidak ada koneksi internet"
    }
}