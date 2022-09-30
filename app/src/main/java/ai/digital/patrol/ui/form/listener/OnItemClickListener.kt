/*
 *     Digital Patrol Guard
 *     OnItemClickListener.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:37 AM
 */

package ai.digital.patrol.ui.form.listener

import ai.digital.patrol.data.entity.*

interface OnZoneClickListener {
    fun onItemClicked(_zone: Zone)
}
interface OnCheckpointClickListener {
    fun onItemClicked(_checkpoint: Checkpoint)
}
interface OnObjectClickListener {
    fun onItemClicked(_objectPatrol: ObjectPatrol)
}
interface OnReportClickListener {
    fun onItemClicked(_reportDetail: ReportDetail)
}
interface OnPhotoClickListener{
    fun onPhotoDeleteBtnClick(photoReport: PhotoReport)
}