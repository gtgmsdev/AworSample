package com.gamatechno.awor.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.gamatechno.awor.R
import com.gamatechno.awor.network.ApiService
import com.gamatechno.awor.network.NetworkConfig
import com.gamatechno.awor.utils.SafeClickListener
import com.gamatechno.awor.utils.SessionManager
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_loading.*

abstract class BaseFragment : Fragment() {
    private var apiService: ApiService? = null
    private var sessionManager: SessionManager? = null
    private var contexts: Context? = null
    private var activitys: Activity? = null
    protected var defaultInterval: Int = 1000
    protected var lastTimeClicked: Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.contexts = context
        this.activitys = context as Activity
    }

    fun getApiService(): ApiService? {
        if (apiService == null) {
            apiService = NetworkConfig.getAPIService(contexts!!)
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
            sessionManager = SessionManager(contexts!!)
        }
        return sessionManager
    }

    fun getProgLoading(msg: String, ctx: Context) : Dialog {
        val loading = Dialog(ctx)
        loading.setContentView(R.layout.dialog_loading)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.loadMessage.text = msg
        loading.setCanceledOnTouchOutside(false)
        loading.setCancelable(false)
        return loading
    }

    fun showToast(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showInfo(msg: String, isSuccess: Boolean){
        val customDialog = Dialog(contexts!!)
        customDialog.setContentView(R.layout.dialog_info)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.setCancelable(false)
        customDialog.setCanceledOnTouchOutside(false)

        val btInfo: Button = customDialog.findViewById(R.id.btInfo)
        val ivInfo: ImageView = customDialog.findViewById(R.id.ivInfo)
        val tvInfo: TextView = customDialog.findViewById(R.id.tvInfo)

        if (isSuccess) {
            ivInfo.setImageResource(R.drawable.ic_approved)
        }
        else{
            ivInfo.setImageResource(R.drawable.ic_alert_error)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvInfo.text = Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            tvInfo.text = Html.fromHtml(msg)
        }
        btInfo.setSafeOnClickListener { customDialog.dismiss() }

        customDialog.show()
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    val permissionStorage = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val permCodeStorage = 1113

    fun isGrandPermission(permList : Array<String>) : Boolean{
        var allowed = true
        for (perm in permList) {
            allowed = allowed && ActivityCompat.checkSelfPermission(
                contexts!!, perm
            ) == PackageManager.PERMISSION_GRANTED
        }
        return allowed
    }
}