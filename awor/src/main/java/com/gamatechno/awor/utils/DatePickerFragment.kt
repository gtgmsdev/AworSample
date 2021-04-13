package com.gamatechno.awor.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseBottomSheet
import kotlinx.android.synthetic.main.sheet_date_picker.view.*

class DatePickerFragment(
    var type: String,
    var selectDate: Long?,
    var minDate: Long?,
    var maxDate: Long?,
    var listener: DatePickerListener
) : BaseBottomSheet() {
    var rootView: View? = null
    private var date = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.sheet_date_picker, container, false)

        if (minDate != null) {
            rootView?.calendarView?.minDate = minDate as Long
        }

        if (maxDate != null) {
            rootView?.calendarView?.maxDate = maxDate as Long
        }

        if (selectDate != null){
            rootView?.calendarView?.setDate(selectDate!!, true, true)
            rootView?.btApply?.isEnabled = true
            date = DateFormatUtil.longToString(selectDate!!, "yyyy-MM-dd").toString()
        }
        else{
            rootView?.btApply?.isEnabled = false
        }

        rootView?.calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val fixMonth = month + 1
            val formatMont = if (fixMonth < 10) {
                "0$fixMonth"
            } else {
                "$fixMonth"
            }

            val formatDay: String = if (dayOfMonth < 10) {
                "0$dayOfMonth"
            } else {
                "$dayOfMonth"
            }
            date = "$year-$formatMont-$formatDay"
            rootView?.btApply?.isEnabled = date != ""
        }

        rootView?.btApply?.setSafeOnClickListener {
            listener.onDateSelect(type, date)
            dismiss()
        }

        return rootView
    }

    override fun getTheme(): Int {
        return R.style.BaseBottomSheetDialog
    }

    interface DatePickerListener {
        fun onDateSelect(type: String, date: String)
    }
}
