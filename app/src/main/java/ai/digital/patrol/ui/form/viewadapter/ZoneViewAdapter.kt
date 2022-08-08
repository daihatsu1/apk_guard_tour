/*
 *     Digital Patrol Guard
 *     ZoneViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 10:56 PM
 */

package ai.digital.patrol.ui.form.viewadapter

import ai.digital.patrol.R
import ai.digital.patrol.model.Zone
import ai.digital.patrol.ui.form.listener.OnZoneClickListener
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ZoneViewAdapter(private val listener: OnZoneClickListener) : RecyclerView.Adapter<ZoneViewAdapter.ZoneViewHolder>(){
    private var zone = mutableListOf<Zone>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_zone, parent, false)
        return ZoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: ZoneViewHolder, position: Int) {
        val item = zone[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return zone.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListZone(zone: List<Zone>) {
        this.zone = zone.toMutableList()
        Log.d("ZONE", this.zone.toString())
        notifyDataSetChanged()
    }

    inner class ZoneViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val zoneName: TextView = itemView.findViewById(R.id.tv_zone_name)
        private var date: TextView = itemView.findViewById(R.id.tv_zone_date)
        private val shift: TextView = itemView.findViewById(R.id.tv_zone_shift)
        fun bind(zone: Zone, listener: OnZoneClickListener){
            date.text = zone.date
            shift.text = zone.shift
            zoneName.text = zone.plant +"- ZONE "+ zone.zone_id
            itemView.setOnClickListener{
                listener.onItemClicked(zone)
            }
        }
    }
}

