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

    fun getDataTemuanAPI(): LiveData<List<Temuan>> {
        return repository!!.getDataTemuanAPI()
    }

    fun getDataTemuan(): LiveData<List<Temuan>>? {
        return repository!!.getTemuan()
    }

    fun getDataTemuanByCheckpoint(checkpointId: String): LiveData<List<Temuan>>? {
        return repository!!.getTemuanByCheckPoint(checkpointId)
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
    fun getAllCheckpoint(): LiveData<List<Checkpoint>>? {
        return repository?.getAllCheckpoint()
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

    suspend fun getReportBySyncToken(sync_token: String): Report? {
        return repository?.getReportBySyncToken(sync_token)
    }

    suspend fun getReportByCheckpointId(checkpointId: String): Report? {
        return repository?.getReportByCheckpointId(checkpointId)
    }

    fun addReportDetail(report: Report, dataReportDetail: ReportDetail) {
        repository?.addReportDetail(report, dataReportDetail)
    }
    fun addReportNormalDetail(report: Report, dataReportDetail: ReportDetail) {
        repository?.addReportNormalDetail(report, dataReportDetail)
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

    suspend fun insertReport(report: Report): Report? {
         return repository?.addReport(report)
    }

    fun getReport(sync_token: String): LiveData<Report>? {
        return repository?.getReport(sync_token)
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

    fun setPatrolActivity(patrolActivity: PatrolActivity) {
        repository?.setPatrolActivity(patrolActivity)
    }
    fun setPatrolActivityStart(idJadwal: String) {
        repository?.setPatrolActivityStart(idJadwal)
    }
    fun setPatrolRunningShift(status:String) {
        repository?.setRunningPatrolShift(status)
    }
    fun setPatrolActivityDone(idJadwal: String) {
        repository?.setPatrolActivityDone(idJadwal)
    }
    fun getPatrolActivity(idJadwal:String): LiveData<PatrolActivity>? {
        return repository?.getPatrolActivity(idJadwal)
    }
    fun getPatrolActivityApi(idJadwal:String): LiveData<PatrolActivity>? {
        return repository?.getPatrolActivityApi(idJadwal = idJadwal)
    }

    fun resetDataReport() {
        repository?.resetDataReport()
    }


}