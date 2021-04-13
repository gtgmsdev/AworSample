package com.gamatechno.awor.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gamatechno.awor.R
import com.gamatechno.awor.base.BaseAdapter
import com.gamatechno.awor.home.model.Rows
import kotlinx.android.synthetic.main.item_list_room_meet.view.*
import java.text.SimpleDateFormat
import java.util.*

class ListMeetAdapter(
    private var userId: String,
    private val edit: (Rows) -> Unit,
    private val share: (Rows) -> Unit,
    private val start: (Rows) -> Unit
) : BaseAdapter<ListMeetAdapter.Holder>() {

    var list: MutableList<Rows> = arrayListOf()

    fun updateList(list: MutableList<Rows>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(rows: Rows) {
            with(itemView) {

                var tgl = "-"

                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("id"))
                format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

                format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id"))
                format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                format.timeZone = TimeZone.getTimeZone("GMT+7")

                tgl = format.format(rows.createdAt)

                tvMeetName.text = rows.name
                tvMeetCode.text = rows.code
                tvMeetCreate.text = "Dibuat pada $tgl"

                if (userId.equals(rows.owner!!.id, ignoreCase = true))
                    ivEditRoom.visibility = View.VISIBLE
                else
                    ivEditRoom.visibility = View.GONE

                if (!rows.expired.equals("", ignoreCase = true))
                    llAction.visibility = View.INVISIBLE
                else
                    llAction.visibility = View.VISIBLE

                ivEditRoom.setSafeOnClickListener {
                    edit(rows)
                }

                ivShareRoom.setSafeOnClickListener {
                    share(rows)
                }

                btnStartRoom.setSafeOnClickListener {
                    start(rows)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_room_meet, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */
    private fun add(r: Rows) {
        list.add(r)
        notifyItemInserted(list.size - 1)
    }

    fun addAll(moveResults: List<Rows>) {
        for (result in moveResults) {
            add(result)
        }
    }

    private fun remove(r: Rows) {
        val position = list.indexOf(r)
        if (position > -1) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        while (itemCount > 0) {
            remove(getItem())
        }
    }

    private fun getItem(): Rows {
        return list[0]
    }
}