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
import ai.digital.patrol.data.entity.ObjectPatrol
import ai.digital.patrol.ui.form.listener.OnObjectClickListener
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ObjectViewAdapter(private val listener: OnObjectClickListener) :
    RecyclerView.Adapter<ObjectViewAdapter.ObjectViewHolder>() {
    private var _objectPatrol = mutableListOf<ObjectPatrol>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_object, parent, false)
        context = parent.context
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        val item = _objectPatrol[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return _objectPatrol.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(_objectPatrol: List<ObjectPatrol>) {
        this._objectPatrol = _objectPatrol.toMutableList()
        Log.d("OBJECT", this._objectPatrol.toString())
        notifyDataSetChanged()
    }

    inner class ObjectViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val objectName: TextView = itemView.findViewById(R.id.item_tv_object_name)
        private val objectBg: MaterialCardView = itemView.findViewById(R.id.item_cv_object_card)
        private val objectIcon: ImageView = itemView.findViewById(R.id.item_imv_object_icon)

        fun bind(_objectPatrol: ObjectPatrol, listener: OnObjectClickListener) {
            objectName.text = _objectPatrol.nama_objek
            when (_objectPatrol.is_normal) {
                false -> {
                    objectBg.setCardBackgroundColor(ContextCompat.getColor(context, R.color.alert))
                    objectBg.strokeColor = ContextCompat.getColor(context, R.color.alert)

                    objectName.setTextColor(ContextCompat.getColor(context, R.color.white))

                    objectIcon.setImageResource(R.drawable.outline_error_outline_24)
                    objectIcon.setColorFilter(ContextCompat.getColor(context, R.color.white))
                    itemView.setOnClickListener {
                        Toast.makeText(
                            context,
                            "Object ditandai sebagai temuan pada patroli sebelumnya",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                true -> {
                    objectIcon.setImageResource(R.drawable.outline_check_circle_outline_24)
                    objectIcon.setColorFilter(ContextCompat.getColor(context, R.color.white))
                    objectBg.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.success
                        )
                    )
                    objectBg.strokeColor = ContextCompat.getColor(context, R.color.success)

                    objectName.setTextColor(ContextCompat.getColor(context, R.color.white))
                    objectIcon.setColorFilter(ContextCompat.getColor(context, R.color.white))
                    itemView.setOnClickListener(null)
                }

                else -> {
                    objectIcon.setImageResource(R.drawable.baseline_chevron_right_white_36dp)
                    objectBg.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    objectBg.strokeColor = ContextCompat.getColor(context, R.color.info)

                    objectName.setTextColor(ContextCompat.getColor(context, R.color.grey_900))
                    objectIcon.setColorFilter(ContextCompat.getColor(context, R.color.info))
                    itemView.setOnClickListener {
                        listener.onItemClicked(_objectPatrol)
                    }
                }
            }
        }
    }
}

