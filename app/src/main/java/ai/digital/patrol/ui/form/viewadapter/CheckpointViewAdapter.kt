/*
 *     Digital Patrol Guard
 *     CheckpointViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 11:42 PM
 */

package ai.digital.patrol.ui.form.viewadapter

import ai.digital.patrol.R
import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.ui.form.listener.OnCheckpointClickListener
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class CheckpointViewAdapter(private val listener: OnCheckpointClickListener) : RecyclerView.Adapter<CheckpointViewAdapter.CheckPointViewHolder>(){
    private var checkpoints = mutableListOf<Checkpoint>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckPointViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkpoint, parent, false)
        context = parent.context
        return CheckPointViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckPointViewHolder, position: Int) {
        val item = checkpoints[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return checkpoints.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(checkpoints: List<Checkpoint>) {
        this.checkpoints = checkpoints.toMutableList()
        Log.d("LIST CHECKPOINT", this.checkpoints.toString())
        notifyDataSetChanged()
    }

    inner class CheckPointViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val checkpointName: TextView = itemView.findViewById(R.id.tv_checkpoint_name)
        private val checkpointNFC: TextView = itemView.findViewById(R.id.tv_checkpoint_nfc)
        private val checkpointBg: MaterialCardView = itemView.findViewById(R.id.checkpoint_bg)
        private val checkpointPatrolStatus : TextView = itemView.findViewById(R.id.tv_checkpoint_patrol_status)
        private val icon: ImageView = itemView.findViewById(R.id.ic_chevron_right)
        
        fun bind(checkpoint: Checkpoint, listener: OnCheckpointClickListener){
            checkpointName.text = checkpoint.check_name
            checkpointNFC.text = checkpoint.no_nfc
            when (checkpoint.patrol_status) {
                false -> {
                    checkpointPatrolStatus.text = context.getString(R.string.status_patrol_done)
                    checkpointPatrolStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
                    checkpointName.setTextColor(ContextCompat.getColor(context, R.color.white))
                    checkpointBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.info))
                    checkpointBg.strokeColor =ContextCompat.getColor(context, R.color.info)
                    icon.visibility = GONE
                    itemView.setOnClickListener(null)
                }
                true -> {
                    checkpointPatrolStatus.text = context.getString(R.string.status_patrol_ongoing)
                    checkpointBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.warning))
                    checkpointBg.strokeColor =ContextCompat.getColor(context, R.color.warning)
                    icon.visibility = VISIBLE

                    itemView.setOnClickListener{
                        listener.onItemClicked(checkpoint)
                    }
                }
                else -> {
                    checkpointPatrolStatus.text = context.getString(R.string.status_patrol_pending)
                    checkpointBg.strokeColor =ContextCompat.getColor(context, R.color.info)
                    icon.visibility = VISIBLE
                    itemView.setOnClickListener{
                        listener.onItemClicked(checkpoint)
                    }
                }
            }

        }
    }
}

