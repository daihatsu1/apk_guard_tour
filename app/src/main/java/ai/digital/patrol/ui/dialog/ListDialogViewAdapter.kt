/*
 *     Digital Patrol Guard
 *     ZoneViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 10:56 PM
 */

package ai.digital.patrol.ui.dialog

import ai.digital.patrol.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListDialogViewAdapter :
    RecyclerView.Adapter<ListDialogViewAdapter.ItemViewHolder>() {
    private var items = ArrayList<String>()
    lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog, parent, false)
        context = parent.context
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(stringList: ArrayList<String>) {
        this.items = stringList
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val tvItem: TextView = itemView.findViewById(R.id.tv_item_name)
        fun bind(stringItem: String) {
            tvItem.text = stringItem
        }
    }
}

