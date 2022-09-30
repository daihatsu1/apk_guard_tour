/*
 *     Digital Patrol Guard
 *     ReportDetailViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:59 AM
 */

package ai.digital.patrol.ui.form.viewadapter

import ai.digital.patrol.R
import ai.digital.patrol.data.entity.Temuan
import ai.digital.patrol.helper.loadUrl
import ai.digital.patrol.ui.form.listener.OnReportClickListener
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TemuanViewAdapter(private val listener: OnReportClickListener?) :
    RecyclerView.Adapter<TemuanViewAdapter.TemuanViewHolder>() {
    private var _temuan = mutableListOf<Temuan>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemuanViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return TemuanViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemuanViewHolder, position: Int) {
        val item = _temuan[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return _temuan.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(_temuan: List<Temuan>) {
        this._temuan = _temuan.toMutableList()
//        Log.d("OBJECT", this._temuan.toString())
        notifyDataSetChanged()
    }

    inner class TemuanViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val reportTitle: TextView = itemView.findViewById(R.id.tv_report_name)
        private val reportDesc: TextView = itemView.findViewById(R.id.tv_report_desc)
        private val status: View = itemView.findViewById(R.id.statusview)
        private val image: ImageView = itemView.findViewById(R.id.imv_temuan)

        fun bind(_temuan: Temuan, listener: OnReportClickListener?) {
            reportTitle.text = _temuan.object_name
            reportDesc.text = _temuan.description
            status.setBackgroundResource(R.color.warning)
            if (_temuan.image?.isNotEmpty() == true){
                image.loadUrl(_temuan.image)
            }


        }
    }
}

