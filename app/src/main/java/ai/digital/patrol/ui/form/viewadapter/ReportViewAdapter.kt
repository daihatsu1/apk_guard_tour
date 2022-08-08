/*
 *     Digital Patrol Guard
 *     ReportViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:59 AM
 */

package ai.digital.patrol.ui.form.viewadapter

import ai.digital.patrol.R
import ai.digital.patrol.model.Report
import ai.digital.patrol.ui.form.listener.OnReportClickListener
import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ReportViewAdapter(private val listener: OnReportClickListener) :
    RecyclerView.Adapter<ReportViewAdapter.ReportViewHolder>() {
    private var _report = mutableListOf<Report>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = _report[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return _report.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(_report: List<Report>) {
        this._report = _report.toMutableList()
        Log.d("OBJECT", this._report.toString())
        notifyDataSetChanged()
    }

    inner class ReportViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val reportTitle: TextView = itemView.findViewById(R.id.tv_report_name)
        private val reportDesc: TextView = itemView.findViewById(R.id.tv_report_desc)
        private val status: View = itemView.findViewById(R.id.statusview)

        fun bind(_report: Report, listener: OnReportClickListener) {
            reportTitle.text = _report.title
            reportDesc.text = _report.description
            if (_report.status) {
                status.setBackgroundResource(R.color.green)
            } else {
                status.setBackgroundResource(R.color.primaryColor)
            }
            itemView.setOnClickListener {
                listener.onItemClicked(_report)
            }
        }
    }
}

