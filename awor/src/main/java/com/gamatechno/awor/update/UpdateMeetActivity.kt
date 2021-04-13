package com.gamatechno.awor.update

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.InputType
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseActivity
import com.gamatechno.awor.home.HomeActivity
import com.gamatechno.awor.home.model.Rows
import com.gamatechno.awor.update.model.ResponseUpdateMeet
import com.gamatechno.awor.update.presenter.UpdateMeetPresenter
import com.gamatechno.awor.update.view.UpdateMeetView
import com.gamatechno.awor.utils.DateConvert
import com.gamatechno.awor.utils.Validate
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_meet.*
import kotlinx.android.synthetic.main.activity_update_meet.*
import kotlinx.android.synthetic.main.activity_update_meet.etRoomName
import kotlinx.android.synthetic.main.activity_update_meet.etRoomPassword
import kotlinx.android.synthetic.main.activity_update_meet.swAnonymous
import kotlinx.android.synthetic.main.activity_update_meet.swReuse
import kotlinx.android.synthetic.main.activity_update_meet.tvTimeEnd
import kotlinx.android.synthetic.main.activity_update_meet.tvTimeStart
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UpdateMeetActivity : BaseActivity(),
    UpdateMeetView {

    private var updateMeetPresenter: UpdateMeetPresenter? = null

    private var data: Rows? = null

    private var loading: Dialog? = null

    private var anonym = false
    private var change = false
    private var reuse = false

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var myDay = 0
    var myMonth = 0
    var myYear = 0
    var myHour = 0
    var myMinute = 0

    var timeStart = ""
    var timeEnd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_meet)

        loading = getProgLoading(resources.getString(R.string.process), this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbarTitle.text = "Update Meeting"

        data = Gson().fromJson(intent.getStringExtra("data"), Rows::class.java)

        updateMeetPresenter = UpdateMeetPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        updateMeetPresenter!!.attachView(this)

        updateUI()

        cbChangePassword.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            change = if (isChecked) {
                etLayoutPass.isEnabled = true
                etRoomPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                etRoomPassword.setText("")
                true
            } else {
                etLayoutPass.isEnabled = false
                etRoomPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                etRoomPassword.setText("pass")
                false
            }
        }

        swAnonymous.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            anonym = isChecked
        }

        tvTimeStart.setOnClickListener {
            tvTimeStart.text = ""
            setStart()
        }

        tvTimeEnd.setOnClickListener {
            tvTimeEnd.text = ""
            setEnd()
        }

        swReuse.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            reuse = isChecked
        }

        btnUpdateRoom.setOnClickListener { prepareRoom() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return false
        }
        lastTimeClicked = SystemClock.elapsedRealtime()

        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI() {
        etRoomName.setText(data!!.name)
        etLayoutPass.isEnabled = false
        if (data!!.password!!) {
            etRoomPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            etRoomPassword.setText("pass")
        } else {
            etRoomPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            etRoomPassword.setText("Biarkan kosong untuk tanpa kata sandi")
        }
        swAnonymous.isChecked = data!!.anonymous!!
        swReuse.isChecked = data!!.reusable!!
        var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("id"))
        try {
            var tglStart = "-"
            var tglEnd = "-"
            format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            format = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id"))
            format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            format.timeZone = TimeZone.getTimeZone("GMT+7")
            tglStart = format.format(data!!.start!!)
            tglEnd = format.format(data!!.end!!)
            tvTimeStart.text = tglStart
            tvTimeEnd.text = tglEnd
            Log.d("KENALOG", "coba deh $tglStart $tglEnd")
        } catch (e: ParseException) {
            e.printStackTrace()
            tvTimeStart.text = ""
            tvTimeEnd.text = ""
        }
        timeStart = data!!.start.toString()!!
        timeEnd = data!!.end.toString()!!
        anonym = data!!.anonymous!!
        reuse = data!!.reusable!!
    }

    private fun setStart() {
        val calendar = Calendar.getInstance()
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                myYear = year
                myDay = dayOfMonth
                myMonth = month + 1
                val c = Calendar.getInstance()
                hour = c[Calendar.HOUR]
                minute = c[Calendar.MINUTE]
                val timePickerDialog =
                    TimePickerDialog(this,
                        { _: TimePicker?, hourOfDay: Int, minute: Int ->
                            myHour = hourOfDay
                            myMinute = minute
                            timeStart = String.format("%d-%02d-%02d",
                                myYear,
                                myMonth,
                                myDay) + "T" + String.format("%02d:%02d:00.000Z",
                                myHour,
                                myMinute)

                            Log.d("KENALOG", "sebelum convert $timeStart")
                            try {
                                timeStart = DateConvert.test(timeStart)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            tvTimeStart.text = String.format("%02d-%02d-%d",
                                myDay,
                                myMonth,
                                myYear) + " " + String.format("%02d:%02d", myHour, myMinute)
                        }, hour, minute, DateFormat.is24HourFormat(this))
                timePickerDialog.show()
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun setEnd() {
        val calendar = Calendar.getInstance()
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                myYear = year
                myDay = dayOfMonth
                myMonth = month + 1
                val c = Calendar.getInstance()
                hour = c[Calendar.HOUR]
                minute = c[Calendar.MINUTE]
                val timePickerDialog =
                    TimePickerDialog(this,
                        { _: TimePicker?, hourOfDay: Int, minute: Int ->
                            myHour = hourOfDay
                            myMinute = minute
                            timeEnd = String.format("%d-%02d-%02d",
                                myYear,
                                myMonth,
                                myDay) + "T" + String.format("%02d:%02d:00.000Z",
                                myHour,
                                myMinute)

                            Log.d("KENALOG", "sebelum convert $timeEnd")
                            try {
                                timeEnd = DateConvert.test(timeEnd)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            tvTimeEnd.text = String.format("%02d-%02d-%d",
                                myDay,
                                myMonth,
                                myYear) + " " + String.format("%02d:%02d", myHour, myMinute)
                        }, hour, minute, DateFormat.is24HourFormat(this))
                timePickerDialog.show()
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun prepareRoom() {
        val nameRoom: String = etRoomName.text.toString()
        val passRoom: String = etRoomPassword.text.toString()
        if (!Validate.cek(etRoomName)) {
            val params = HashMap<String, Any>()
            params["anonymous"] = anonym.toString()
            params["name"] = nameRoom
            params["start"] = timeStart
            params["end"] = timeEnd
            params["password"] = passRoom
            params["passwordUpdate"] = change.toString()
            params["reusable"] = reuse.toString()
            Log.d("KENALOG", "mau update $params")
            updateMeetPresenter!!.update(
                getSessionManager()!!.getToken()!!,
                data!!.id!!,
                params
            )
        }
    }

    override fun onSuccessUpdate(responseUpdateMeet: ResponseUpdateMeet) {
        Log.d("KENALOG", "update sukses $responseUpdateMeet")
        AlertDialog.Builder(this)
            .setMessage("Ubah data pertemuan berhasil!")
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                startActivity(Intent(this,
                    HomeActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                ).putExtra("fromOhter", true))
                finish()
            }
            .show()
    }

    override fun onErrorUpdate(msg: String) {
        Log.d("KENALOG", "update error $msg")
    }

    override fun dismissLoading() {
        loading!!.dismiss()
    }

    override fun showLoading() {
        loading!!.show()
    }

    override fun onUnAuthorize() {
    }

}