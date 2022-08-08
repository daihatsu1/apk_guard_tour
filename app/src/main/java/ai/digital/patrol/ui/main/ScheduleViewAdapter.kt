/*
 *     Digital Patrol Guard
 *     ScheduleViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.main

import ai.digital.patrol.R
import ai.digital.patrol.model.Schedule
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class ScheduleViewAdapter : RecyclerView.Adapter<ScheduleViewAdapter.ScheduleViewHolder>(){
    var schedules = mutableListOf<Schedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patrol_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = schedules[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSchedule(schedule: List<Schedule>) {
        this.schedules = schedule.toMutableList()
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(item: View): RecyclerView.ViewHolder(item) {
        var date: TextView = itemView.findViewById(R.id.tv_schedule_date)
        private val shift: TextView = itemView.findViewById(R.id.tv_schedule_shift)
        private val day: TextView = itemView.findViewById(R.id.tv_schedule_day)
        fun bind(schedule: Schedule){
            date.text = schedule.date
            shift.text = schedule.shift
            day.text = schedule.day
        }
    }
}

