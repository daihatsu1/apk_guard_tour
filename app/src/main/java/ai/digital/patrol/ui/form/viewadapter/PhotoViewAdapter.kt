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
import ai.digital.patrol.ui.form.listener.OnPhotoClickListener
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PhotoViewAdapter(private val onPhotoClickListener: OnPhotoClickListener) :
    RecyclerView.Adapter<PhotoViewAdapter.PhotoReportViewHolder>() {
    private var photoReportList = mutableListOf<PhotoReport>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoReportViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        context = parent.context
        return PhotoReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoReportViewHolder, position: Int) {
        val item = photoReportList[position]
        holder.bind(item, onPhotoClickListener)
    }

    override fun getItemCount(): Int {
        return photoReportList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(photoReport: List<PhotoReport>) {
        this.photoReportList = photoReport.toMutableList()
        notifyDataSetChanged()
    }

    inner class PhotoReportViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val photo: ImageView = itemView.findViewById(R.id.iv_reporting_photo)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.btn_remove_photo)
        fun bind(photoReport: PhotoReport, listener: OnPhotoClickListener) {
            photo.loadUri(photoReport.photoUri, false)
            deleteBtn.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.resources.getString(R.string.photo_dialog_confirm_delete))
                    .setMessage(context.resources.getString(R.string.messaage_photo_dialog_confirm_delete))
                    .setNeutralButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
                        // Respond to neutral button press
                        dialog.dismiss()
                    }
                    .setPositiveButton(context.resources.getString(R.string.delete)) { dialog, _ ->
                        // Respond to positive button press
                        photoReportList.remove(photoReport)
                        notifyDataSetChanged()
                        listener.onPhotoDeleteBtnClick(photoReport)
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}


