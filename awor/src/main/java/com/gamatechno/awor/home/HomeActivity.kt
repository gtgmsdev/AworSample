package com.gamatechno.awor.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamatechno.awor.BuildConfig
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseActivity
import com.gamatechno.awor.create.CreateMeetActivity
import com.gamatechno.awor.global.model.ResponseAuth
import com.gamatechno.awor.global.model.ResponseCheckMeet
import com.gamatechno.awor.global.presenter.AuthPresenter
import com.gamatechno.awor.global.presenter.CheckMeetPresenter
import com.gamatechno.awor.global.view.AuthView
import com.gamatechno.awor.global.view.CheckMeetView
import com.gamatechno.awor.home.adapter.ListMeetAdapter
import com.gamatechno.awor.home.model.ResponseListMeet
import com.gamatechno.awor.home.model.ResponsePasswordMeet
import com.gamatechno.awor.home.model.Rows
import com.gamatechno.awor.home.presenter.ListMeetPresenter
import com.gamatechno.awor.home.presenter.PasswordMeetPresenter
import com.gamatechno.awor.home.view.ListMeetView
import com.gamatechno.awor.home.view.PasswordMeetView
import com.gamatechno.awor.network.ApiConst
import com.gamatechno.awor.prepare.PrepareMeetActivity
import com.gamatechno.awor.update.UpdateMeetActivity
import com.gamatechno.awor.utils.Validate
import com.gamatechno.awor.utils.maskedEditText.MaskedEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.ParseException
import java.text.SimpleDateFormat

class HomeActivity : BaseActivity(),
    AuthView,
    ListMeetView,
    CheckMeetView,
    PasswordMeetView {
    private var authPresenter: AuthPresenter? = null
    private var listMeetPresenter: ListMeetPresenter? = null
    private var checkMeetPresenter: CheckMeetPresenter? = null
    private var passwordMeetPresenter: PasswordMeetPresenter? = null

    private var listMeetAdapter: ListMeetAdapter? = null

    private var loading: Dialog? = null

    private var max = 1
    private var current = 1

    private var shareBody: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d("KENALOG", ApiConst.BASE_URL)

        loading = getProgLoading(resources.getString(R.string.process), this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbarTitle.text = "Meeting"

        authPresenter = AuthPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        authPresenter!!.attachView(this)

        listMeetPresenter = ListMeetPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        listMeetPresenter!!.attachView(this)

        checkMeetPresenter = CheckMeetPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        checkMeetPresenter!!.attachView(this)

        passwordMeetPresenter = PasswordMeetPresenter(
            getApiService()!!, getSubscribeOn()!!, getObserveOn()!!
        )
        passwordMeetPresenter!!.attachView(this)

        Log.d("KENALOG", "sini gak? ${intent.getBooleanExtra("fromOther", false)}")

        if (intent.getBooleanExtra("fromOther", false)) {
            val data = HashMap<String, Any>()
            data["access_token"] = "2f1c30005f72290daf120ec25565278c"
            data["origin_token"] = intent.getStringExtra("token")!!.replace("Bearer ", "")
            Log.d("KENALOG", data.toString())
            authPresenter!!.auth(data)
        } else {
            loadData(current)
        }

        listMeetAdapter = ListMeetAdapter(
            getSessionManager()!!.getUserId()!!,
            {
                startActivity(Intent(this, UpdateMeetActivity::class.java).putExtra("data",
                    Gson().toJson(it)))
            },
            {
                passwordMeetPresenter!!.password(getSessionManager()!!.getToken()!!, it.code!!)

                var tgl = "-"

                if (it.start == null)
                    tgl = "-"
                else {
                    val start: String
                    val end: String
                    var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    try {
                        format = SimpleDateFormat("dd MMM yyyy HH:mm")
                        start = format.format(it.start!!)
                        end = format.format(it.end!!)
                        tgl = "$start - $end"
                    } catch (e: ParseException) {
                        Log.e("KENALOG", e.message!!)
                        tgl = "-"
                    }
                }

                shareBody =
                    """Anda diundang untuk bergabung ke pertemuan ${it.name} pada $tgl. Untuk bergabung klik tautan ini: ${BuildConfig.base_api}meet/${it.code} atau gunakan kode pertemuan: ${it.code}"""
            },
            {
                val params = HashMap<String, Any?>()
                params["code"] = it.code
                params["guestName"] = getSessionManager()!!.getEmpName()
                params["guestAvatar"] = getSessionManager()!!.getEmpPhoto()
                params["commit"] = "false"
                Log.d("KENALOG", params.toString())
                checkMeetPresenter!!.check(getSessionManager()!!.getToken()!!, params)
            }
        )

        rvList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = listMeetAdapter
        }

        cvCreateMeet.setSafeOnClickListener {
            startActivity(Intent(this, CreateMeetActivity::class.java))
        }

        cvJoinMeet.setSafeOnClickListener {
            showDialogCode()
        }

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                current = 1

                val data = HashMap<String, Any>()
                data["limit"] = 10
                data["page"] = current
                data["term"] = query
                Log.d("KENALOG", "pagenya $data")
                listMeetPresenter!!.list(getSessionManager()!!.getToken()!!, data)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // filter recycler view when text is changed
                /*current = 1

                val data = HashMap<String, Any>()
                data["limit"] = 10
                data["page"] = current
                data["term"] = newText
                Log.d("KENALOG", "pagenya $data")
                listMeetPresenter!!.list(getSessionManager()!!.getToken()!!, data)*/
                return false
            }
        })

        swList.setOnRefreshListener {
            loadData(current)
        }

        btnNext.setSafeOnClickListener {
            if (max != current && max > current) {
                current++
                loadData(current)
            }
        }

        btnPrev.setSafeOnClickListener {
            if (current != 1) {
                current--
                loadData(current)
            }
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

    override fun onSuccessAuth(responseAuth: ResponseAuth) {
        getSessionManager()!!.setToken(responseAuth.token!!)
        getSessionManager()!!.setUserId(responseAuth.user!!.id!!)
        getSessionManager()!!.setEmpName(responseAuth.user!!.name!!)
        getSessionManager()!!.setEmpEmail(responseAuth.user!!.email!!)
        getSessionManager()!!.setEmpPhoto(responseAuth.user!!.picture!!)

        Log.d("KENALOG", "tokennya ${getSessionManager()!!.getToken()!!}")
        Log.d("KENALOG", "login sukses $responseAuth")

        loadData(current)
    }

    override fun onErrorAuth(msg: String) {
        Log.d("KENALOG", "error nya $msg")
        showInfo(msg, false, null)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessList(responseListMeet: ResponseListMeet) {
        Log.d("KENALOG", "list sukses $responseListMeet")

        listMeetAdapter!!.clear()
        listMeetAdapter!!.addAll(responseListMeet.rows!! as List<Rows>)
        if (listMeetAdapter!!.list.isNotEmpty()) {

            rvList.visibility = View.VISIBLE
            llPage.visibility = View.VISIBLE
            llNoData.visibility = View.GONE

            max = if (responseListMeet.total!! % 10 == 0)
                responseListMeet.count!! / 10
            else responseListMeet.count!! / 10 + 1

            updateUI()

            if (responseListMeet.total!! == responseListMeet.count)
                tvJumlah.text = "${responseListMeet.count} Pertemuan"
            else
                tvJumlah.text = "${responseListMeet.count} dari ${responseListMeet.total} Pertemuan"

        } else {
            rvList.visibility = View.GONE
            llPage.visibility = View.GONE
            llNoData.visibility = View.VISIBLE

            tvJumlah.text = "${responseListMeet.count} dari ${responseListMeet.total} Pertemuan"
        }
    }

    override fun onErrorList(msg: String) {
        Log.d("KENALOG", "error list $msg")
        showInfo(msg, false, null)
    }

    override fun onSuccessCheck(responseCheckMeet: ResponseCheckMeet) {
        Log.d("KENALOG", "check sukses $responseCheckMeet")
        startActivity(Intent(this, PrepareMeetActivity::class.java)
            .putExtra("data", Gson().toJson(responseCheckMeet)))
    }

    override fun onErrorCheck(msg: String) {
        Log.d("KENALOG", "check error $msg")
        showInfo(msg, false, null)
    }

    override fun onSuccessPassword(responsePasswordMeet: ResponsePasswordMeet) {
        Log.d("KENALOG", "password success $responsePasswordMeet")
        shareThis(shareBody!!, responsePasswordMeet.password!!)
    }

    override fun onErrorPassword(msg: String) {
        Log.d("KENALOG", "password error $msg")
    }

    override fun dismissLoading() {
        swList.isRefreshing = false
        loading!!.dismiss()
    }

    override fun showLoading() {
        loading!!.show()
    }

    override fun onUnAuthorize() {

    }

    private fun loadData(current: Int) {
        Log.d("KENALOG", "tokennya ${getSessionManager()!!.getToken()!!}")
        val data = HashMap<String, Any>()
        data["limit"] = 10
        data["page"] = current
        Log.d("KENALOG", "pagenya $data")
        listMeetPresenter!!.list(getSessionManager()!!.getToken()!!, data)
        svSearch.setQuery("", false)
        svSearch.clearFocus()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        tvHal.text = "Hal. $current dari $max"
        if (current == 1 && max > current) {
            btnNext.isEnabled = true
            btnPrev.isEnabled = false
        } else if (current != 1 && max > current) {
            btnNext.isEnabled = true
            btnPrev.isEnabled = true
        } else if (current != 1 && max == current) {
            btnNext.isEnabled = false
            btnPrev.isEnabled = true
        } else {
            btnNext.isEnabled = false
            btnPrev.isEnabled = false
        }
    }

    private fun showDialogCode() {
        var change = false
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_input_code_room)
        customDialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.setCancelable(true)
        customDialog.setCanceledOnTouchOutside(true)

//        val etRoomCode: MaskedEditText = customDialog.findViewById(R.id.et_room_code)
        val etRoomCode: EditText = customDialog.findViewById(R.id.et_room_code)
        val etURLRoom: EditText = customDialog.findViewById(R.id.et_link_meet)
        val cbChangeURL: CheckBox = customDialog.findViewById(R.id.cb_change_url)
        val btnJoinRoom: Button = customDialog.findViewById(R.id.btn_join_room)

        cbChangeURL.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                change = true
                etRoomCode.visibility = View.GONE
                etURLRoom.visibility = View.VISIBLE
            } else {
                change = false
                etRoomCode.visibility = View.VISIBLE
                etURLRoom.visibility = View.GONE
            }
        }

        btnJoinRoom.setOnClickListener {
            if (change) {
                if (!Validate.cek(etURLRoom)) {
                    if (etURLRoom.text.toString().contains(BuildConfig.base_api)) {
                        val code =
                            etURLRoom.text.toString().replace(BuildConfig.base_api + "meet/", "")
                        Log.d("KENALOG", "code nya $code")
                        val params = HashMap<String, Any?>()
                        params["code"] = code
                        params["guestName"] = getSessionManager()!!.getEmpName()
                        params["guestAvatar"] = getSessionManager()!!.getEmpPhoto()
                        params["commit"] = "false"
                        checkMeetPresenter!!.check(getSessionManager()!!.getToken()!!, params)
                        customDialog.dismiss()
                    } else {
                        Log.d("KENALOG", "code nya ${etURLRoom.text}")
                        etURLRoom.error = "URL tidak valid"
                    }
                }
            } else {
                if (!Validate.cek(etRoomCode)) {
                    val params = HashMap<String, Any?>()
                    params["code"] = etRoomCode.text.toString()
                    params["guestName"] = getSessionManager()!!.getEmpName()
                    params["guestAvatar"] = getSessionManager()!!.getEmpPhoto()
                    params["commit"] = "false"
                    checkMeetPresenter!!.check(getSessionManager()!!.getToken()!!, params)
                    customDialog.dismiss()
                }
            }
        }

        customDialog.show()
    }

    private fun shareThis(shareBody: String, password: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Awor")
        if (password.equals("", ignoreCase = true) || password.equals("false",
                ignoreCase = true)
        ) sharingIntent.putExtra(
            Intent.EXTRA_TEXT, shareBody) else {
            val content = "$shareBody dan masukkan password: $password"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

}