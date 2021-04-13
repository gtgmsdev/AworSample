package com.gamatechno.awor.jitsi

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.facebook.react.modules.core.PermissionListener
import com.gamatechno.awor.BuildConfig
import com.gamatechno.awor.R
import com.gamatechno.awor.home.HomeActivity
import com.gamatechno.awor.jitsi.model.JoinRoom
import com.gamatechno.awor.jitsi.model.JoinUpdate
import com.gamatechno.awor.socket.SocketSingleton
import com.gamatechno.awor.utils.SessionManager
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.Transport
import kotlinx.android.synthetic.main.dialog_end.*
import org.jitsi.meet.sdk.*
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Example //
class JitsiActivity : FragmentActivity(), JitsiMeetActivityInterface {
    private var view: JitsiMeetView? = null

    private var sessionManager: SessionManager? = null
    private var mSocket: Socket? = null

    private val gson = Gson()

    private var countDownTimer: CountDownTimer? = null


    private var code = ""
    private var subject = ""
    private var participantId = ""
    private var baseUrl = ""
    private var moderator = false

    private var url = ""

    private var audio = false
    private var video = false

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        JitsiMeetActivityDelegate.onActivityResult(
            this, requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Anda ingin keluar dari sesi Room ini?")
            .setPositiveButton("Ya") { _: DialogInterface?, _: Int ->
                endMeet()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager == null) {
            sessionManager = SessionManager(this)
        }

        url = intent.getStringExtra("url")!!
        audio = intent.getBooleanExtra("audio", false)
        video = intent.getBooleanExtra("video", false)

        Log.d("KENALOG", "URL Meet $url")

        val baseMeet: String = url.substringBefore("?jwt=")
        code = baseMeet.reversed().substring(0, 14).reversed()

        Log.d("KENALOG", "Code Meet $code")

        subject = url.substringAfter("&subject=").substringBefore("&participantId=")
        participantId = url.substringAfter("&participantId=").substringBefore("&moderator=")
        baseUrl = url.substringAfter("&server=")
        moderator = url.substringAfter("&moderator=").substringBefore("&server=").toBoolean()

        Log.d("KENALOG", "participant Id $participantId moderator $moderator")

        if (url != null && !url.equals("", ignoreCase = true)) {
            val singleton = SocketSingleton(this,
                baseUrl,
                "2f1c30005f72290daf120ec25565278c",
                sessionManager!!.getToken(),
                code)
            mSocket = singleton.socket
        } else endMeet()

        view = JitsiMeetView(this)

        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(url)
            .setWelcomePageEnabled(false)
            .setFeatureFlag("add-people.enabled", false)
            .setFeatureFlag("calendar.enabled", false)
            .setFeatureFlag("close-captions.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("live-streaming.enabled", false)
            .setFeatureFlag("meeting-name.enabled", false)
            .setFeatureFlag("meeting-password.enabled", false)
            .setFeatureFlag("recording.enabled", false)
            .setFeatureFlag("video-share.enabled", false)
            .setFeatureFlag("pip.enabled", true)
            .setFeatureFlag("call-integration.enabled", false)
            .setAudioMuted(!audio)
            .setVideoMuted(!video)
            .build()

        view!!.join(options)

        view!!.listener = object : JitsiMeetViewListener {
            override fun onConferenceJoined(map: Map<String, Any>) {
                Log.e("onConferenceJoined", map.toString())
                checkSocket()

                connectSocket()
            }

            override fun onConferenceTerminated(map: Map<String, Any>) {
                Log.e("onConferenceTerminated", map.toString())

                if (map.size == 1) {
                    if (moderator) {
                        showEndMeeting()
                    } else {
                        endMeet()
                    }
                } else if (!map["error"].toString()
                        .equals("connection.droppedError", ignoreCase = true)
                ) {
                    endMeet()
                } else {
                    AlertDialog.Builder(this@JitsiActivity)
                        .setMessage("Anda terputus dari jaringan.\nCoba periksa koneksi Anda.")
                        .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                            endMeet()
                        }.show()
                }
            }

            override fun onConferenceWillJoin(map: Map<String, Any>) {
                Log.e("onConferenceWillJoin", map.toString())
            }
        }


        setContentView(view)
    }

    fun checkSocket() {
        if (!mSocket!!.connected()) {
            Log.d("KENALOG", "otw connect?")
            mSocket!!.connect()
        } else {
            Log.d("KENALOG", "ora ora ora")
        }
    }

    private fun connectSocket() {
        Log.d("KENALOG", "Kesini? " + BuildConfig.base_api + "/meetroom")

        mSocket!!.io().on(Manager.EVENT_TRANSPORT) { args: Array<Any> ->
            Log.d("KENALOG", "CONNECT?? " + args[0])
            val transport =
                args[0] as Transport
            transport.on(Transport.EVENT_REQUEST_HEADERS
            ) { args1: Array<Any> ->
                Log.d("KENALOG", "CONNECT??? " + args1[0])
                val headers =
                    args1[0] as MutableMap<String, List<String>>
                headers["client-id"] = Arrays.asList("awor-oid-eovizlite-6fcb8395")
            }
        }
        mSocket!!.on(Socket.EVENT_CONNECT) { args: Array<Any?>? ->
            Log.d("KENALOG", "CONNECT POO " + gson.toJson(args))
            Log.d("KENALOG", "isConnected " + mSocket!!.connected())
            try {
                mSocket!!.emit("SESSION_JOIN",
                    JSONObject(gson.toJson(JoinRoom(code, participantId))),
                    Ack { args1: Array<Any?> ->
                        Log.d("KENALOG",
                            "JOIN $args1")
                    })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        mSocket!!.on(Socket.EVENT_MESSAGE) { args: Array<Any?> ->
            Log.d("KENALOG", "MSG $args")
        }

        mSocket!!.on(Socket.EVENT_CONNECTING) { args: Array<Any?>? ->
            Log.d("KENALOG", "CONNECTING ")
        }

        mSocket!!.on(Socket.EVENT_ERROR) { args: Array<Any?> ->
            Log.d("KENALOG", "ERROR $args")
        }

        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any?> ->
            Log.d("KENALOG", "CONNECT ERROR\n$args")
            mSocket!!.disconnect()
        }

        mSocket!!.on("SESSION_JOIN_UPDATE") { args1: Array<Any?>? ->
            Log.d("KENALOG", "JOIN UPDATE " + gson.toJson(args1))
            this.runOnUiThread { prepareLimit(args1) }
        }

        mSocket!!.on("SESSION_JOIN_MAX") { args: Array<Any?>? ->
            Log.d("KENALOG", "SESSION_JOIN_MAX " + gson.toJson(args))
        }

        mSocket!!.on("SESSION_END") { args: Array<Any?>? ->
            Log.d("KENALOG", "END kah? " + gson.toJson(args))
            //activity.runOnUiThread(this::endMeet);
            this.runOnUiThread { goToEnd() }
        }
    }

    private fun prepareLimit(args1: Array<Any?>?) {
        val joinUpdate: JoinUpdate =
            gson.fromJson(gson.toJson(args1).substringAfter("[").substringBefore("]"),
                JoinUpdate::class.java)
        val limitTime: Int =
            joinUpdate.nameValuePairs!!.limit!!.nameValuePairs!!.durationLimitTime!!
        var tgl = "-"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("id"))
        format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        tgl = format.format(joinUpdate.nameValuePairs!!.start!!)
        Log.d("KENALOG", "tgl nya $tgl")
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var date: Date? = null
        try {
            date = dateFormat.parse(tgl) as Date
        } catch (e: ParseException) {
            Log.d("KENALOG", "ParseException nya " + e.message)
            e.printStackTrace()
        }
        val currentTime = Calendar.getInstance().time
        if (limitTime != -1) prepareCountDown(limitTime, currentTime.time, date!!.time) else {
            /* snackbar = Snackbar.make(view, "Anda Berhasil Bergabung.", Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.setMargins(8, 112, 8, 8);
            params.gravity = Gravity.TOP;
            snackbarView.setLayoutParams(params);
            snackbar.show();*/
        }
    }

    private fun prepareCountDown(limitTime: Int, current: Long, date: Long) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(current - date)
        val different = limitTime - minutes
        Log.d("KENALOG", "Different time $different")
        if (countDownTimer != null)
            countDownTimer!!.cancel()
        countDownTimer = object : CountDownTimer(TimeUnit.MINUTES.toMillis(different), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("KENALOG", "Count down $millisUntilFinished")
//                snackbar.dismiss()
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                Log.d("KENALOG", "Count down minute$minutes")
                if (minutes <= 9) {
                    if (minutes <= 2) {
                        /*snackbar = Snackbar.make(view!!,
                            "Pertemuan akan berakhir sebentar lagi.",
                            Snackbar.LENGTH_INDEFINITE)*/
                    } else {
                        /*snackbar = Snackbar.make(view!!,
                            "Pertemuan akan berakhir dalam $minutes menit lagi.",
                            Snackbar.LENGTH_INDEFINITE)*/
                    }
                    /*val snackbarView: View = snackbar.getView()
                    val params = snackbarView.layoutParams as FrameLayout.LayoutParams
                    params.setMargins(8, 112, 8, 8)
                    params.gravity = Gravity.TOP
                    snackbarView.layoutParams = params
                    snackbar.show()*/
                }
            }

            override fun onFinish() {
                Log.d("KENALOG", "DONE")
                if (moderator) {
                    mSocket!!.emit("SESSION_FORCE_END", JSONObject(),
                        Ack { args: Array<Any?> ->
                            Log.d("KENALOG",
                                "SESSION_FORCE_END kah? $args")
                        })
                }
                JitsiMeetActivityDelegate.onHostDestroy(this@JitsiActivity)
                mSocket!!.disconnect()
                this@JitsiActivity.finish()
            }
        }.start()
    }

    private fun goToEnd() {
        Log.d("KENALOG", "TO END")
        AlertDialog.Builder(this@JitsiActivity)
            .setMessage("Sesi telah berakhir")
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                endMeet()
            }.show()
    }

    private fun endMeet() {
        if (countDownTimer != null)
            countDownTimer!!.cancel()

        JitsiMeetActivityDelegate.onHostDestroy(this)
        if (mSocket != null)
            mSocket!!.disconnect()

        JitsiMeetActivityDelegate.onHostDestroy(this)
        if (view != null)
            view!!.leave()

        val intent: Intent
        intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAndRemoveTask()
    }

    override fun onUserLeaveHint() {
        if (supportsPiPMode()) view!!.enterPictureInPicture()
    }

    private fun supportsPiPMode(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    override fun onDestroy() {
        super.onDestroy()
        view!!.dispose()
        view = null
        if (mSocket != null) mSocket!!.disconnect()
        JitsiMeetActivityDelegate.onHostDestroy(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        JitsiMeetActivityDelegate.onNewIntent(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        JitsiMeetActivityDelegate.requestPermissions(this, p0, p1, p2)

    }

    override fun onResume() {
        super.onResume()
        JitsiMeetActivityDelegate.onHostResume(this)
    }

    override fun onStop() {
        super.onStop()
        JitsiMeetActivityDelegate.onHostPause(this)
    }

    fun showEndMeeting() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_end)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.setCancelable(false)
        customDialog.setCanceledOnTouchOutside(false)

        customDialog.btnEndAll.setOnClickListener {
            mSocket!!.emit("SESSION_FORCE_END", JSONObject(),
                Ack { args: Array<Any?> ->
                    Log.d("KENALOG",
                        "SESSION_FORCE_END kah? $args")
                } as Ack)
            endMeet()
            customDialog.dismiss()
        }

        customDialog.btnEndMe.setOnClickListener {
            endMeet()
        }

        customDialog.show()
    }
}
