/*
 *     Digital Patrol Guard
 *     LoginRepository.kt
 *     Created by ImamSyahrudin on 13/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/13/22, 2:02 PM
 */

package ai.digital.patrol.data.repository

import ai.digital.patrol.DatabaseClient
import ai.digital.patrol.data.dao.*
import ai.digital.patrol.data.entity.*
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.networking.ServiceGenerator
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class PatrolDataRepository(val dataSource: DatabaseClient) {

    // in-memory cache of the user object
    internal var zone: Zone? = null
    internal var checkpoint: Checkpoint? = null
    internal var objectPatrol: ObjectPatrol? = null

    private val zoneDao: ZoneDao? = DatabaseClient.getInstance()?.appDatabase?.zoneDao()
    private val checkpointDao: CheckpointDao? =
        DatabaseClient.getInstance()?.appDatabase?.checkpointDao()
    private val objectPatrolDao: ObjectPatrolDao? =
        DatabaseClient.getInstance()?.appDatabase?.objectPatrolDao()
    private val eventDao: EventDao? =
        DatabaseClient.getInstance()?.appDatabase?.eventDao()
    private val patrolDataDao: PatrolDataDao? =
        DatabaseClient.getInstance()?.appDatabase?.patrolDataDao()

    private val runnerScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext =
            Dispatchers.IO // no job added i.e + SupervisorJob()
    }

    init {
        zone = null
        checkpoint = null
        objectPatrol = null
    }

    fun insertDataPatrol(zones: List<Zone?>?) {
        runnerScope.launch {
            patrolDataDao?.insertPatrolDate(zones as List<Zone?>)
        }
    }

    fun insertZone(zone: Zone) {
        runnerScope.launch {
            zoneDao?.insert(zone)
        }
    }

    fun insertCheckpoint(checkpoint: Checkpoint) {
        runnerScope.launch {
            checkpointDao?.insert(checkpoint)
        }
    }

    fun insertObjectPatrol(objectPatrol: ObjectPatrol) {
        runnerScope.launch {
            objectPatrolDao?.insert(objectPatrol)
        }
    }

    @JvmName("getPatrolDataAPI1")
    fun getPatrolDataAPI(): LiveData<List<Zone>> {
        return patrolDataRequest
    }

    fun getZone(): LiveData<List<Zone>>? {
        return zoneDao?.zones
    }

    fun getCheckpointByZone(zoneId: String): LiveData<List<Checkpoint>>? {
        return checkpointDao?.checkpointByZones(zoneId)

    }

    fun getObjectByCheckpoint(checkpointId: String): LiveData<List<ObjectPatrol>>? {
        return objectPatrolDao?.getObjectByCheckpoint(checkpointId)

    }

    fun getEventByObject(objectId: String): LiveData<List<Event>> {
        return eventDao?.getEventByObjectId(objectId)!!
    }

    fun addReportDetail(dataReportDetail: ReportDetail) {
        runnerScope.launch {
            patrolDataDao?.insertReportDetail(dataReportDetail)
            patrolDataDao?.flagObjectAsTemuan(dataReportDetail.admisecsgp_mstobj_objek_id)
        }
    }
    fun setZoneOnPatrol(zoneId: String) {
        runnerScope.launch {
            patrolDataDao?.setZoneOnPatrol(zoneId)
        }
    }
    fun setZoneOnPatrolDone(zoneId: String) {
        runnerScope.launch {
            patrolDataDao?.setZoneOnPatrolDone(zoneId)
        }
    }
    fun checkInCheckpoint(checkpointId: String) {
        runnerScope.launch {
            patrolDataDao?.checkInCheckpoint(Utils.createdAt("yyyy-MM-dd HH:mm:ss"), checkpointId)
            patrolDataDao?.setCheckpointOnPatrol(checkpointId)
        }

    }

    fun checkOutCheckpoint(checkpointId: String) {
        runnerScope.launch {
            patrolDataDao?.checkOutCheckpoint(Utils.createdAt("yyyy-MM-dd HH:mm:ss"), checkpointId)
            patrolDataDao?.setCheckpointDonePatrol(checkpointId)
        }

    }

    fun checkReport(checkpointId: String): LiveData<Report>? {
        return patrolDataDao?.checkReport(checkpointId)
    }

    fun addReport(report: Report) {
        runnerScope.launch {
            patrolDataDao?.insertReport(report)

//            patrolDataDao?.flagObjectAsTemuan(dataReportDetail.admisecsgp_mstobj_objek_id)
        }
    }

    fun syncReport(report: Report, reportDetail: List<ReportDetail>?){
        runnerScope.launch {
            patrolDataDao?.insertDataReport(report, reportDetail)
//            patrolDataDao?.flagObjectAsTemuan(dataReportDetail.admisecsgp_mstobj_objek_id)
        }
    }

    fun getUnSyncReport(): List<Report> {

        val unSyncReport = patrolDataDao!!.getUnSyncReport()
        val unSyncReportList = emptyList<Report>().toMutableList()
        if (unSyncReport != null) {
            for (data in unSyncReport) {
                val report = data.report
                report.detailTemuan = data.reportDetail
                unSyncReportList.add(report)
            }
        }
        return  unSyncReportList
    }
    fun getReportDetail(): LiveData<List<ReportDetailObject>>? {
        return   patrolDataDao!!.getReportDetail()
    }

    private val patrolDataRequest: LiveData<List<Zone>>
        get() {
            val restInterface = ServiceGenerator.createService()
            val surveyDataCall = restInterface.getPatrolData()
            surveyDataCall!!.enqueue(object : Callback<List<Zone>> {
                override fun onResponse(
                    call: Call<List<Zone>>,
                    response: Response<List<Zone>>
                ) {
                    if (response.isSuccessful) {
                        insertDataPatrol(response.body())
                    }
                }

                override fun onFailure(call: Call<List<Zone>>, t: Throwable) {
                    Log.d("ZONE", "FAIL....", t)

                }
            })
            return patrolDataDao?.getData()!!
        }

    companion object {
        @Volatile
        private var instance: PatrolDataRepository? = null

        @JvmStatic
        fun getInstance(): PatrolDataRepository? {
            if (instance == null) {
                instance = PatrolDataRepository(DatabaseClient.getInstance()!!)
            }
            return instance
        }
    }

}