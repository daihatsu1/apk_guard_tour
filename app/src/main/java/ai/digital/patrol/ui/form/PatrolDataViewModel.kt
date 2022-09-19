/*
 *     Digital Patrol Guard
 *     PatrolDataViewModel.kt
 *     Created by ImamSyahrudin on 13/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/13/22, 4:16 PM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.data.entity.*
import ai.digital.patrol.data.repository.PatrolDataRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PatrolDataViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatrolDataViewModel(application) as T
        }
    }

    private val repository = PatrolDataRepository.getInstance()

    fun getPatrolDataAPI(): LiveData<List<Zone>> {
        return repository!!.getPatrolDataAPI()
    }

    fun insertPatrolData(zones: List<Zone>) {
        return repository!!.insertDataPatrol(zones)
    }


    fun getZones(): LiveData<List<Zone>>? {
        return repository?.getZone()
    }

    fun getCheckpointByZone(zone_id: String): LiveData<List<Checkpoint>>? {
        return repository?.getCheckpointByZone(zone_id)
    }

    fun getObjectByCheckpoint(checkpointId: String): LiveData<List<ObjectPatrol>>? {
        return repository?.getObjectByCheckpoint(checkpointId)
    }

    fun getEventByObject(objectId: String): LiveData<List<Event>>? {
        return repository?.getEventByObject(objectId)

    }

    fun checkReport(checkpointId: String): LiveData<Report>? {
        return repository?.checkReport(checkpointId)
    }

    fun addReportDetail(dataReportDetail: ReportDetail) {
        repository?.addReportDetail(dataReportDetail)
    }

    fun setZoneOnPatrol(zoneId: String) {
        repository?.setZoneOnPatrol(zoneId)
    }

    fun setZoneOnPatrolDone(zoneId: String) {
        repository?.setZoneOnPatrolDone(zoneId)
    }

    fun checkInCheckpoint(checkpointId: String) {
        repository?.checkInCheckpoint(checkpointId)
    }

    fun checkOutCheckpoint(checkpointId: String) {
        repository?.checkOutCheckpoint(checkpointId)
    }

    fun insertReport(report: Report) {

        repository?.addReport(report)
    }

    fun getUnSyncReport(): List<Report>? {
        return repository?.getUnSyncReport()
    }

    fun syncReport(report: Report, reportDetail: List<ReportDetail>?) {
        repository?.syncReport(report, reportDetail)
    }

    fun getReportDetail(): LiveData<List<ReportDetailObject>>? {
        return repository?.getReportDetail()
    }


}