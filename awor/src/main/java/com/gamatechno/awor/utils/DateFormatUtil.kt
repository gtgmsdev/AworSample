package com.gamatechno.awor.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtil {
    fun stringToLong(date: String, dateFormat: String): Long? {
        var stdF: Long? = null
        val df = SimpleDateFormat(dateFormat, Locale.getDefault())
        Log.d("KENALOG", "date $date")
        try {
            stdF = df.parse(date)!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.d("KENALOG", "error nya " + e.message)
        }

        return stdF
    }

    fun longToString(milis: Long, dateFormat: String): String? {
        val date = Date()
        date.time = milis

        var dateS = ""

        try {
            val df = SimpleDateFormat(dateFormat, Locale.getDefault())
            dateS = df.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dateS
    }

    fun formatDisplay(date: String, oldFormat: String, newFormat: String): String {
        var display = ""
        val d: Date
        var df = SimpleDateFormat(oldFormat, Locale.getDefault())
        try {
            d = df.parse(date)!!
            df = SimpleDateFormat(newFormat, Locale.getDefault())
            display = df.format(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return display
    }
}