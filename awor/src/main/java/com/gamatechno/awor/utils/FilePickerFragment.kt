package com.gamatechno.awor.utils


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.sheet_file_picker.view.*

class FilePickerFragment(
    var noDocument: Boolean = false,
    var listener: FilePickerListener
) : BaseBottomSheet() {
    var rootView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val dialogg = dialog as BottomSheetDialog
            val bottomSheet =
                dialogg.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
            bottomSheet?.let { BottomSheetBehavior.from(it).skipCollapsed = true }
            bottomSheet?.let { BottomSheetBehavior.from(it).isHideable = true }
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.sheet_file_picker, container, false)

        if (noDocument){
            rootView!!.tvFilePicker.text = resources.getString(R.string.change_profil_foto)
            rootView!!.btDokumen.visibility = View.GONE
        }
        else{
            rootView!!.tvFilePicker.text = resources.getString(R.string.upload_file)
        }

        rootView?.btKamera?.setSafeOnClickListener {
            listener.onFilePickerSelect("kamera")
            dismiss()
        }

        rootView?.btGambar?.setSafeOnClickListener {
            listener.onFilePickerSelect("gambar")
            dismiss()
        }

        rootView?.btDokumen?.setSafeOnClickListener {
            listener.onFilePickerSelect("dokumen")
            dismiss()
        }

        return rootView
    }

    interface FilePickerListener{
        fun onFilePickerSelect(type: String)
    }
}
