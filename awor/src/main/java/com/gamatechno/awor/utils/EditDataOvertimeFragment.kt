package com.gamatechno.awor.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseBottomSheet
//import com.gamatechno.awor.lembur.model.LemburMultipleModel
import kotlinx.android.synthetic.main.sheet_edit_overtime.*

class EditDataOvertimeFragment(
//    var lemburMultipleModel: LemburMultipleModel
) : BaseBottomSheet() {
    var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.sheet_edit_overtime, container, false)

        /*etTanggalOvertimeEdit.setText(lemburMultipleModel.tglOvertime)
        etWaktuMulaiEdit.setText(lemburMultipleModel.wktMulai)
        etWaktuSelesaiEdit.setText(lemburMultipleModel.wktSelesai)
        etKeteranganEdit.setText(lemburMultipleModel.keterangan)*/

        return rootView
    }

    override fun getTheme(): Int {
        return R.style.BaseBottomSheetDialog
    }
}