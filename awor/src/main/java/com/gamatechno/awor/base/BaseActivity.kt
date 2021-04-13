package com.gamatechno.awor.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.gamatechno.awor.R
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NetworkConfig
import com.gamatechno.awor.utils.SafeClickListener
import com.gamatechno.awor.utils.SessionManager
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_loading.*

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {
    private var apiService: ApiService? = null
    private var sessionManager: SessionManager? = null
    protected var defaultInterval: Int = 1000
    protected var lastTimeClicked: Long = 0

    private var jobScheduler: JobScheduler? = null
    private var jobInfo: JobInfo? = null

    fun getApiService(): ApiService? {
        if (apiService == null) {
            apiService = NetworkConfig.getAPIService(this)
        }
        return apiService
    }

    fun getSubscribeOn(): Scheduler? {
        return Schedulers.io()
    }

    fun getObserveOn(): Scheduler? {
        return AndroidSchedulers.mainThread()
    }

    fun getSessionManager(): SessionManager? {
        if (sessionManager == null) {
            sessionManager = SessionManager(this)
        }
        return sessionManager
    }

    fun getProgLoading(msg: String, ctx: Context): Dialog {
        val loading = Dialog(ctx)
        loading.setContentView(R.layout.dialog_loading)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.loadMessage.text = msg
        loading.setCanceledOnTouchOutside(false)
        loading.setCancelable(false)
        return loading
    }

    fun showInfo(msg: String, isSuccess: Boolean, intent: Intent?) {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_info)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.setCancelable(false)
        customDialog.setCanceledOnTouchOutside(false)

        val btInfo: Button = customDialog.findViewById(R.id.btInfo)
        val ivInfo: ImageView = customDialog.findViewById(R.id.ivInfo)
        val tvInfo: TextView = customDialog.findViewById(R.id.tvInfo)

        if (isSuccess) {
            ivInfo.setImageResource(R.drawable.ic_approved)
        } else {
            ivInfo.setImageResource(R.drawable.ic_alert_error)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvInfo.text = Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            tvInfo.text = Html.fromHtml(msg)
        }
        btInfo.setSafeOnClickListener {
            customDialog.dismiss()
            if (intent != null) {
                startActivity(intent)
            }
        }

        customDialog.show()
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    fun isGrandPermission(permList: Array<String>): Boolean {
        var allowed = true
        for (perm in permList) {
            allowed = allowed && ActivityCompat.checkSelfPermission(
                this, perm
            ) == PackageManager.PERMISSION_GRANTED
        }
        return allowed
    }

    private val permCodeList = 1111

    private val permissionStorage = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!this.localClassName.contains("SplashActivity")) {
            if (isGrandPermission(permissionStorage)) {
            } else {
                ActivityCompat.requestPermissions(this, permissionStorage, permCodeList)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permCodeList) {
            var allowed = true
            for (res in grantResults) {
                allowed = allowed && res == PackageManager.PERMISSION_GRANTED
            }

            if (!allowed) {
                ActivityCompat.requestPermissions(this, permissionStorage, permCodeList)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}