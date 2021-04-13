package com.gamatechno.awor.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class SessionManager(
    var context: Context
) {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val privateMode = 0

    private val prefName = "aworPresencePref"
    private val isLogin = "isLogin"
    private val token = "token"
    private val empName = "empName"
    private val empPhoto = "empPhoto"
    private val empEmail = "empEmail"

    private val filPresenceYear = "filPresenceYear"

    private val filExchangeStatus = "filExchangeStatus"
    private val filExchangeYear = "filExchangeYear"

    private val filLeaveStatus = "filLeaveStatus"
    private val filLeaveYear = "filLeaveYear"
    private val filLeaveType = "filLeaveType"

    private val filOvertimeStatus = "filOvertimeStatus"
    private val filOvertimeYear = "filOvertimeYear"

    private val filTeamTglStart = "filTeamTglStart"
    private val filTeamTglEnd = "filTeamTglEnd"
    private val filTeamEmp = "filTeamEmp"

    private val latitude = "latitude"
    private val longitude = "longitude"
    private val userId = "userId"

    init {
        pref = context.getSharedPreferences(prefName, privateMode)
        editor = pref.edit()
    }

    fun setLogin(value: Boolean) {
        editor.putBoolean(isLogin, value)
        editor.commit()
    }

    fun isLogin(): Boolean {
        return pref.getBoolean(isLogin, false)
    }

    fun setEmpName(value: String) {
        editor.putString(empName, value)
        editor.commit()
    }

    fun getEmpName(): String? {
        return pref.getString(empName, "")
    }

    fun setEmpPhoto(value: String) {
        editor.putString(empPhoto, value)
        editor.commit()
    }

    fun getEmpPhoto(): String? {
        return pref.getString(empPhoto, "")
    }

    fun setToken(value: String) {
        editor.putString(token, value)
        editor.commit()
    }

    fun getToken(): String? {
        return pref.getString(token, "")
    }

    //FILTER PRESENCE
    fun setEmpEmail(value: String) {
        editor.putString(empEmail, value)
        editor.commit()
    }

    fun getEmpEmail(): String? {
        return pref.getString(empEmail, "")
    }

    fun setFilPresenceYear(value: Int) {
        editor.putInt(filPresenceYear, value)
        editor.commit()
    }

    fun getFilPresenceYear(): Int? {
        return pref.getInt(filPresenceYear, Calendar.getInstance().get(Calendar.YEAR))
    }

    fun clearEmpEmail() {
        editor.remove(empEmail)
        editor.commit()
    }

    fun clearFilPresenceYear() {
        editor.remove(filPresenceYear)
        editor.commit()
    }

    //FILTER EXCHANGE
    fun setFilExchangeStatus(value: Int) {
        editor.putInt(filExchangeStatus, value)
        editor.commit()
    }

    fun getFilExchangeStatus(): Int? {
        return pref.getInt(filExchangeStatus, -1)
    }

    fun setFilExchangeYear(value: Int) {
        editor.putInt(filExchangeYear, value)
        editor.commit()
    }

    fun getFilExchangeYear(): Int? {
        return pref.getInt(filExchangeYear, Calendar.getInstance().get(Calendar.YEAR))
    }

    fun clearFilExchangeStatus() {
        editor.remove(filExchangeStatus)
        editor.commit()
    }

    fun clearFilExchangeYear() {
        editor.remove(filExchangeYear)
        editor.commit()
    }

    //FILTER LEAVE
    fun setFilLeaveStatus(value: Int) {
        editor.putInt(filLeaveStatus, value)
        editor.commit()
    }

    fun getFilLeaveStatus(): Int? {
        return pref.getInt(filLeaveStatus, -1)
    }

    fun setFilLeaveYear(value: Int) {
        editor.putInt(filLeaveYear, value)
        editor.commit()
    }

    fun getFilLeaveYear(): Int? {
        return pref.getInt(filLeaveYear, Calendar.getInstance().get(Calendar.YEAR))
    }

    fun setFilLeaveType(value: Int) {
        editor.putInt(filLeaveType, value)
        editor.commit()
    }

    fun getFilLeaveType(): Int? {
        return pref.getInt(filLeaveType, 0)
    }

    fun clearFilLeaveStatus() {
        editor.remove(filLeaveStatus)
        editor.commit()
    }

    fun clearFilLeaveYear() {
        editor.remove(filLeaveYear)
        editor.commit()
    }

    fun clearFilLeaveType() {
        editor.remove(filLeaveType)
        editor.commit()
    }

    // FILTER OVERTIME
    fun setFilOvertimeStatus(value: Int) {
        editor.putInt(filOvertimeStatus, value)
        editor.commit()
    }

    fun getFilOvertimeStatus(): Int? {
        return pref.getInt(filOvertimeStatus, -1)
    }

    fun clearFilOvertimeStatus() {
        editor.remove(filOvertimeStatus)
        editor.commit()
    }

    fun setFilOvertimeYear(value: Int) {
        editor.putInt(filOvertimeYear, value)
        editor.commit()
    }

    fun getFilOvertimeYear(): Int? {
        return pref.getInt(filOvertimeYear, Calendar.getInstance().get(Calendar.YEAR))
    }

    fun clearFilOvertimeYear() {
        editor.remove(filOvertimeYear)
        editor.commit()
    }

    //FILTER TEAM
    fun setFilTeamTglStart(value: String) {
        editor.putString(filTeamTglStart, value)
        editor.commit()
    }

    fun getFilTeamTglStart(): String? {
        return pref.getString(filTeamTglStart, "")
    }

    fun setFilTeamTglEnd(value: String) {
        editor.putString(filTeamTglEnd, value)
        editor.commit()
    }

    fun getFilTeamTglEnd(): String? {
        return pref.getString(filTeamTglEnd, "")
    }

    fun setFilTeamEmp(value: ArrayList<Int>) {
        editor.putString(filTeamEmp, Gson().toJson(value))
        editor.commit()
    }

    fun getFilTeamEmp(): ArrayList<Int>? {
        val teamEmp: ArrayList<Int>?
        val type = object : TypeToken<ArrayList<Int>?>() {}.type
        teamEmp = Gson().fromJson(pref.getString(filTeamEmp, ""), type)
        return teamEmp
    }

    fun clearFilTeamTglStart() {
        editor.remove(filTeamTglStart)
        editor.commit()
    }

    fun clearFilTeamTglEnd() {
        editor.remove(filTeamTglEnd)
        editor.commit()
    }

    fun clearFilTeamEmp() {
        editor.remove(filTeamEmp)
        editor.commit()
    }

    //LATITUDE, LONGITUDE
    fun setLatitude(value: String) {
        editor.putString(latitude, value)
        editor.commit()
    }

    fun getLatitude(): String? {
        return pref.getString(latitude, "")
    }

    fun setLongitude(value: String) {
        editor.putString(longitude, value)
        editor.commit()
    }

    fun getLongitude(): String? {
        return pref.getString(longitude, "")
    }

    fun setUserId(value: String) {
        editor.putString(userId, value)
        editor.commit()
    }

    fun getUserId(): String? {
        return pref.getString(userId, "")
    }

    fun clearSession(context: Context) {
        Toast.makeText(context, "Session telah habis", Toast.LENGTH_SHORT).show()
        clear(context)
    }

    fun clearSessionLogout(
        context: Context,
        msg: String?
    ) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        clear(context)
    }

    private fun clear(context: Context) {
        editor.clear()
        editor.apply()
        val preferences = context.getSharedPreferences(prefName, privateMode)
        preferences.edit().clear().apply()
        val locale = Locale("in")
        Locale.setDefault(locale)
        /*val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)*/
    }
}