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
import ai.digital.patrol.data.entity.PhotoReport
import ai.digital.patrol.helper.loadUri
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoViewAdapter() : RecyclerView.Adapter<PhotoViewAdapter.PhotoReportViewHolder>(){
    private var photoReportList = mutableListOf<PhotoReport>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoReportViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoReportViewHolder, position: Int) {
        val item = photoReportList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return photoReportList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(photoReport: List<PhotoReport>) {
        this.photoReportList = photoReport.toMutableList()
        notifyDataSetChanged()
    }

    inner class PhotoReportViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val photo: ImageView = itemView.findViewById(R.id.iv_reporting_photo)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.btn_remove_photo)
        fun bind(photoReport: PhotoReport){
            photo.loadUri(photoReport.photoUri, false)
            deleteBtn.setOnClickListener{
                Log.d("deleteBtn", "deleteBtn")
            }
        }
    }
}

