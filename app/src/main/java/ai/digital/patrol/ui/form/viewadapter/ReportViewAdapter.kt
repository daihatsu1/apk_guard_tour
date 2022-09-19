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
import ai.digital.patrol.data.entity.ReportDetail
import ai.digital.patrol.ui.form.listener.OnReportClickListener
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportDetailViewAdapter(private val listener: OnReportClickListener?) :
    RecyclerView.Adapter<ReportDetailViewAdapter.ReportDetailViewHolder>() {
    private var _report = mutableListOf<ReportDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportDetailViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportDetailViewHolder, position: Int) {
        val item = _report[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return _report.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(_report: List<ReportDetail>) {
        this._report = _report.toMutableList()
        Log.d("OBJECT", this._report.toString())
        notifyDataSetChanged()
    }

    inner class ReportDetailViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val reportTitle: TextView = itemView.findViewById(R.id.tv_report_name)
        private val reportDesc: TextView = itemView.findViewById(R.id.tv_report_desc)
        private val status: View = itemView.findViewById(R.id.statusview)

        fun bind(_report: ReportDetail, listener: OnReportClickListener?) {
            reportTitle.text = _report.conditions
            reportDesc.text = _report.description
            status.setBackgroundResource(R.color.warning)

            if (listener != null) {
                itemView.setOnClickListener {
                    listener.onItemClicked(_report)
                }
            }

        }
    }
}

