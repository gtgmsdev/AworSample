package com.gamatechno.awor.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gamatechno.awor.utils.SafeClickListener

abstract class BaseAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}