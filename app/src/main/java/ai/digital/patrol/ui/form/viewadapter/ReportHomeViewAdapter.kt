/*
 *     Digital Patrol Guard
 *     ReportViewAdapter.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:59 AM
 */

package ai.digital.patrol.ui.form.viewadapter

import ai.digital.patrol.R
import ai.digital.patrol.data.entity.ReportDetailObject
import ai.digital.patrol.helper.*
import ai.digital.patrol.ui.form.listener.OnReportClickListener
import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

class ReportDetailHomeViewAdapter(private val listener: OnReportClickListener?) :
    RecyclerView.Adapter<ReportDetailHomeViewAdapter.ReportDetailViewHolder>() {
    private var _report = mutableListOf<ReportDetailObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportDetailViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patrol_reporting_home, parent, false)
        return ReportDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportDetailViewHolder, position: Int) {
        val item = _report[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return _report.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(_reportDetail: List<ReportDetailObject>) {
//        _reportDetail.forEach {
//            this._report.add(it.reportDetail)
//        }
        this._report = _reportDetail.toMutableList()
        Log.d("OBJECT", this._report.toString())
        notifyDataSetChanged()
    }

    inner class ReportDetailViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val reportTitle: TextView = itemView.findViewById(R.id.tv_reporting_object)
        private val reportDesc: TextView = itemView.findViewById(R.id.tv_reporting_note)
        private val image: ImageView = itemView.findViewById(R.id.iv_reporting_photo)
        private val animIcon: LottieAnimationView = itemView.findViewById(R.id.animIcon)

        fun bind(_reportDetailObject: ReportDetailObject, listener: OnReportClickListener?) {
            val reportDetail = _reportDetailObject.reportDetail
            val objectPatrol = _reportDetailObject.objectPatrol

            reportTitle.text = objectPatrol.nama_objek
            reportDesc.text = reportDetail.description
            if (reportDetail.image_1 != null) {
                if (reportDetail.image_1.startsWith("http")) {
                    image.loadUrl(reportDetail.image_1)
                } else if (reportDetail.image_1.startsWith("file")) {
                    image.loadUri(Uri.parse(reportDetail.image_1))
                } else {
                    val images = Utils.decoder(reportDetail.image_1)
                    if (images != null) {
                        image.loadBitmap(images)
                    } else {
                        image.loadDrawable(R.drawable.no_img)
                    }
                }
            }

            if (reportDetail.synced == true) {
                animIcon.setAnimation(R.raw.check_okey_done)
            } else {
                animIcon.setAnimation(R.raw.warning)
            }
            animIcon.repeatCount = LottieDrawable.INFINITE
            animIcon.playAnimation()
        }
    }
}

