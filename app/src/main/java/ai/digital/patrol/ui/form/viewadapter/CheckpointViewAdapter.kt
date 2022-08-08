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
import ai.digital.patrol.model.Checkpoint
import ai.digital.patrol.ui.form.listener.OnCheckpointClickListener
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckpointViewAdapter(private val listener: OnCheckpointClickListener) : RecyclerView.Adapter<CheckpointViewAdapter.CheckPointViewHolder>(){
    private var checkpoints = mutableListOf<Checkpoint>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckPointViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkpoint, parent, false)
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
        Log.d("ZONE", this.checkpoints.toString())
        notifyDataSetChanged()
    }

    inner class CheckPointViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val checkpointName: TextView = itemView.findViewById(R.id.tv_checkpoint_name)
        private val checkpointNFC: TextView = itemView.findViewById(R.id.tv_checkpoint_nfc)
        fun bind(checkpoint: Checkpoint, listener: OnCheckpointClickListener){
            checkpointName.text = checkpoint.check_name
            checkpointNFC.text = checkpoint.no_nfc
            itemView.setOnClickListener{
                listener.onItemClicked(checkpoint)
            }
        }
    }
}

