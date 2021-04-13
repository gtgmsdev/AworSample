package com.gamatechno.awor.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseBottomSheet

class ParticipantPickerFragment : BaseBottomSheet() {
    var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return rootView
    }

    override fun getTheme(): Int {
        return R.style.BaseBottomSheetDialog
    }

    interface ParticipantPickerListener {
        fun onParticipantSelect(msg: String, data: String)
    }
}