package com.gamatechno.awor.utils

import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateConvert {
    fun stringToDate(string: String?): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            dateFormat.parse(string)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    fun dateToString(date: Date?): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }

    @Throws(Exception::class)
    fun test(string: String?): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        df.timeZone = TimeZone.getTimeZone("GMT+7")
        val date = df.parse(string)
        Log.d("KENALOG", "sebelum convert " + df.format(date)) // GMT+7
        df.timeZone = TimeZone.getTimeZone("UTC")
        Log.d("KENALOG", "sebelum convert " + df.format(date)) // UTC
        return df.format(date)
    }
}