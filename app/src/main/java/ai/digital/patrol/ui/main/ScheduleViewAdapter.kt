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
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.helper.Utils
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleViewAdapter : RecyclerView.Adapter<ScheduleViewAdapter.ScheduleViewHolder>() {
    var schedules = mutableListOf<Schedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patrol_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = schedules[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSchedule(schedule: List<Schedule>) {
        this.schedules = schedule.toMutableList()
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private var date: TextView = itemView.findViewById(R.id.tv_schedule_date)
        private val shift: TextView = itemView.findViewById(R.id.tv_schedule_shift)
        private val day: TextView = itemView.findViewById(R.id.tv_schedule_day)
        private val time: TextView = itemView.findViewById(R.id.tv_schedule_time)

        fun bind(schedule: Schedule, position: Int) {
            date.text = schedule.date?.let { Utils.dateFormat(it) }

            time.visibility = VISIBLE
            if( schedule.shift == "LIBUR"){
                time.visibility = GONE
            }
            val timeString =Utils.formatTextTimePatrol(schedule.jam_masuk!!, schedule.jam_pulang!!)
            "Waktu Patroli $timeString".also { time.text = it }

            "SHIFT ${schedule.shift}".also { shift.text = it }
            if (position == 0) {
                "${schedule.plant_name}, HARI INI".also { day.text = it }
            } else {
                "${schedule.plant_name}, SELANJUTNYA".also { day.text = it }
            }
        }
    }
}

