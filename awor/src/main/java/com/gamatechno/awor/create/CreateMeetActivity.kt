package com.gamatechno.awor.create

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseActivity
import com.gamatechno.awor.create.model.ResponseCreateMeet
import com.gamatechno.awor.create.presenter.CreateMeetPresenter
import com.gamatechno.awor.create.view.CreateMeetView
import com.gamatechno.awor.home.HomeActivity
import com.gamatechno.awor.utils.DateConvert
import com.gamatechno.awor.utils.Validate
import kotlinx.android.synthetic.main.activity_create_meet.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

class CreateMeetActivity : BaseActivity(),
    CreateMeetView {

    private var createMeetPresenter: CreateMeetPresenter? = null

    private var loading: Dialog? = null

    private var anonym = false
    private var go = false
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
        setContentView(R.layout.activity_create_meet)

        loading = getProgLoading(resources.getString(R.string.process), this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbarTitle.text = "Create Meeting"

        createMeetPresenter = CreateMeetPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        createMeetPresenter!!.attachView(this)

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

        btnCreate.setOnClickListener { prepareRoom() }
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

                            tvTimeStart.setText(String.format("%02d-%02d-%d",
                                myDay,
                                myMonth,
                                myYear) + " " + String.format("%02d:%02d", myHour, myMinute))
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

                            tvTimeEnd.setText(String.format("%02d-%02d-%d",
                                myDay,
                                myMonth,
                                myYear) + " " + String.format("%02d:%02d", myHour, myMinute))
                        }, hour, minute, DateFormat.is24HourFormat(this))
                timePickerDialog.show()
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun prepareRoom() {
        val nameRoom: String = etRoomName.text.toString()
        val passRoom: String = etRoomPassword.text.toString()
        val cekStart = tvTimeStart.text.toString()
        val cekEnd = tvTimeEnd.text.toString()

        /*if (cekStart.equals(getString(R.string.atur_info_waktu_mulai),
                ignoreCase = true) && cekEnd.equals(getString(R.string.atur_info_waktu_selesai),
                ignoreCase = true)
        ) {
            timeStart = ""
            timeEnd = ""
            go = true
        } else */if (cekStart.equals(getString(R.string.atur_info_waktu_mulai),
                ignoreCase = true) || cekEnd.equals(getString(R.string.atur_info_waktu_selesai),
                ignoreCase = true)
        ) {
            tvTimeStart.error = "This field is required"
            tvTimeEnd.error = "This field is required"
            go = false
        } else go = true
        if (go) {
            if (!Validate.cek(etRoomName)) {
                val params = HashMap<String, Any>()
                params["name"] = nameRoom
                params["anonymous"] = anonym.toString()
                params["password"] = passRoom
                params["start"] = timeStart
                params["end"] = timeEnd
                params["reusable"] = reuse.toString()
                createMeetPresenter!!.create(getSessionManager()!!.getToken()!!, params)
            }
        }
    }

    override fun onSuccessCreate(responseCreateMeet: ResponseCreateMeet) {
        Log.d("KENALOG", "create sukses $responseCreateMeet")
        AlertDialog.Builder(this)
            .setMessage("Pertemuan berhasil dibuat dengan kode : ${responseCreateMeet.code}")
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                startActivity(Intent(this,
                    HomeActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                ).putExtra("fromOhter", true))
                finish()
            }.show()
    }

    override fun onErrorCreate(msg: String) {
        Log.d("KENALOG", "error create $msg")
        showInfo(msg, false, null)
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