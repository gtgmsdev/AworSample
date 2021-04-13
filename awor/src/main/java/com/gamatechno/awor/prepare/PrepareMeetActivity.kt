package com.gamatechno.awor.prepare

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import com.gamatechno.awor.BuildConfig
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseActivity
import com.gamatechno.awor.global.model.ResponseCheckMeet
import com.gamatechno.awor.jitsi.JitsiActivity
import com.gamatechno.awor.prepare.model.ResponsePrepareMeet
import com.gamatechno.awor.prepare.presenter.PrepareMeetPresenter
import com.gamatechno.awor.prepare.view.PrepareMeetView
import com.gamatechno.awor.utils.Validate
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_prepare_meet.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PrepareMeetActivity : BaseActivity(),
    PrepareMeetView {

    private var data: ResponseCheckMeet? = ResponseCheckMeet()

    private var prepareMeetPresenter: PrepareMeetPresenter? = null

    private var loading: Dialog? = null

    private var timeStart = ""
    private var timeEnd = ""

    private var audio = false
    private var video = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare_meet)

        loading = getProgLoading(resources.getString(R.string.process), this)

        prepareMeetPresenter = PrepareMeetPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        prepareMeetPresenter!!.attachView(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbarTitle.text = "Check Meeting"

        data = Gson().fromJson(intent.getStringExtra("data"), ResponseCheckMeet::class.java)

        updateUI()

        swAudio.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            audio = isChecked
        }

        swVideo.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            video = isChecked
        }

        btnJoinRoom.setSafeOnClickListener {
            prepareJoin()
        }
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
        tvRoomName.text = data!!.meetroom!!.name
        tvRoomOwner.text = data!!.meetroom!!.owner!!.name
        tvRoomCode.text = data!!.meetroom!!.code
        if (data!!.meetroom!!.start == null) {
            tvJadwal.visibility = View.GONE
            tvRoomTime.visibility = View.GONE
        } else {
            var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            try {
                format = SimpleDateFormat("dd MMMM yyyy HH:mm")
                timeStart = format.format(data!!.meetroom!!.start)
                timeEnd = format.format(data!!.meetroom!!.end)
                tvRoomTime.text = "$timeStart - $timeEnd"
            } catch (e: ParseException) {
                Log.e("KENALOG", e.message!!)
                tvJadwal.visibility = View.GONE
                tvRoomTime.visibility = View.GONE
            }
        }

        if (!getSessionManager()!!.getEmpEmail()!!.isNullOrEmpty()) {
            when {
                getSessionManager()!!.getEmpEmail()!!
                    .equals(data!!.meetroom!!.owner!!.email!!, ignoreCase = true) -> etLayoutPass.visibility = View.GONE
                data!!.meetroom!!.password!! -> etLayoutPass.visibility = View.VISIBLE
                else -> etLayoutPass.visibility = View.GONE
            }
        } else {
            if (data!!.meetroom!!.password!!)
                etLayoutPass.visibility = View.VISIBLE
            else
                etLayoutPass.visibility = View.GONE
        }

        etLayoutName.visibility = View.GONE
    }

    private fun prepareJoin() {
        var password = ""
        val params = HashMap<String, Any?>()
        params["code"] = data!!.meetroom!!.code
        params["commit"] = "true"
        params["guestName"] = getSessionManager()!!.getEmpName()
        params["guestAvatar"] = getSessionManager()!!.getEmpPhoto()

        if (!data!!.meetroom!!.owner!!.email.equals(getSessionManager()!!.getEmpEmail(),
                ignoreCase = true)
        )
            if (data!!.meetroom!!.password!!)
                if (!Validate.cek(etRoomPassword))
                    password = etRoomPassword.text.toString()

        params["password"] = password

        prepareMeetPresenter!!.prepareMeet(getSessionManager()!!.getToken()!!, params)
    }

    override fun onSuccessPrepare(responsePrepareMeet: ResponsePrepareMeet) {
        Log.d("KENALOG", "sukses prepare $responsePrepareMeet")
        var moderator = false

        Log.d("KENALOG", "email user ${getSessionManager()!!.getEmpEmail()!!}")
        Log.d("KENALOG", "email response ${responsePrepareMeet.meetroom!!.owner!!.email!!}")

        if (getSessionManager()!!.getEmpEmail()!!
                .equals(responsePrepareMeet.meetroom!!.owner!!.email!!, ignoreCase = true)
        ) {
            moderator = true
        } else if (responsePrepareMeet.meetroom!!.moderator!!.isNotEmpty()) {
            for (i in responsePrepareMeet.meetroom!!.moderator!!.indices) {
                if (getSessionManager()!!.getEmpEmail()!!
                        .equals(responsePrepareMeet.meetroom!!.moderator!![i]!!.email!!,
                            ignoreCase = true)
                ) {
                    moderator = true
                    break
                }
            }
        }

        val link =
            "https://${responsePrepareMeet.rtc!!.domain}/${responsePrepareMeet.meetroom!!.code}?jwt=${responsePrepareMeet.accessToken}#jitsi_meet_external_api_id=0&subject=${responsePrepareMeet.meetroom!!.name}&participantId=${responsePrepareMeet.id}&moderator=$moderator&server=${BuildConfig.base_api}"

        startActivity(Intent(this, JitsiActivity::class.java)
            .putExtra("url", link)
            .putExtra("audio", audio)
            .putExtra("video", video))
        finish()
    }

    override fun onErrorPrepare(msg: String) {
        Log.d("KENALOG", "error prepare $msg")
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