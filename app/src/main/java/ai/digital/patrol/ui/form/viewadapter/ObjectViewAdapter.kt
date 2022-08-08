/*
 *     Digital Patrol Guard
 *     ObjectViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:02 AM
 */

package ai.digital.patrol.ui.form.viewadapter

import ai.digital.patrol.R
import ai.digital.patrol.model.Object
import ai.digital.patrol.ui.form.listener.OnObjectClickListener
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ObjectViewAdapter(private val listener: OnObjectClickListener) : RecyclerView.Adapter<ObjectViewAdapter.ObjectViewHolder>(){
    private var _object = mutableListOf<Object>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_object, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        val item = _object[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return _object.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(_object: List<Object>) {
        this._object = _object.toMutableList()
        Log.d("OBJECT", this._object.toString())
        notifyDataSetChanged()
    }

    inner class ObjectViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val objectName: TextView = itemView.findViewById(R.id.tv_object_name)
        private val objectStatus: View = itemView.findViewById(R.id.statusview)
        private val objectIcon: ImageView = itemView.findViewById(R.id.imv_status)

        fun bind(_object: Object, listener: OnObjectClickListener){
            objectName.text = _object.nama_objek
            if (!_object.status){
                objectStatus.setBackgroundResource(R.color.primaryColor)
                objectIcon.visibility = VISIBLE
            }else{
                objectStatus.setBackgroundResource(R.color.green)
                objectIcon.visibility = GONE

                itemView.setOnClickListener{
                    listener.onItemClicked(_object)
                }
            }

        }
    }
}

