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
import ai.digital.patrol.data.entity.Zone
import ai.digital.patrol.ui.form.listener.OnZoneClickListener
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ZoneViewAdapter(private val listener: OnZoneClickListener) :
    RecyclerView.Adapter<ZoneViewAdapter.ZoneViewHolder>() {
    private var zone = mutableListOf<Zone>()
    lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_zone, parent, false)
        context = parent.context
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
        notifyDataSetChanged()
    }

    inner class ZoneViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val zoneBg: MaterialCardView = itemView.findViewById(R.id.zone_bg)
        private val zoneName: TextView = itemView.findViewById(R.id.tv_zone_name)
        private val zonePatrolStatus: TextView = itemView.findViewById(R.id.tv_zone_patrol_status)
        private val icon: ImageView = itemView.findViewById(R.id.ic_chevron_right)

        fun bind(zone: Zone, listener: OnZoneClickListener) {
            zoneName.text = context.getString(R.string.zone, zone.plant_name, zone.zone_name)
            when (zone.patrol_status) {
                false -> {
                    zonePatrolStatus.text = context.getString(R.string.status_patrol_done)
                    zonePatrolStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
                    zoneName.setTextColor(ContextCompat.getColor(context, R.color.white))
                    zoneBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.info))
                    zoneBg.strokeColor = ContextCompat.getColor(context, R.color.info)
                    icon.visibility = View.GONE
                }
                true -> {
                    zonePatrolStatus.text = context.getString(R.string.status_patrol_ongoing)
                    zoneBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.warning))
                    zoneBg.strokeColor = ContextCompat.getColor(context, R.color.warning)
                    icon.visibility = View.VISIBLE

                    itemView.setOnClickListener{
                        listener.onItemClicked(zone)
                    }
                }
                else -> {
                    zonePatrolStatus.text = context.getString(R.string.status_patrol_pending)
                    zoneBg.strokeColor = ContextCompat.getColor(context, R.color.info)
                    icon.visibility = View.VISIBLE
                    itemView.setOnClickListener{
                        listener.onItemClicked(zone)
                    }
                }
            }
        }
    }
}

