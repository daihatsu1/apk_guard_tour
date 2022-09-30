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
import ai.digital.patrol.helper.Utils.toRequestBody
import ai.digital.patrol.networking.ServiceGenerator
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.internal.Util
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

    private val temuanDao: TemuanDao? =
        DatabaseClient.getInstance()?.appDatabase?.temuanDao()

    private val patrolActivityDao: PatrolActivityDao? =
        DatabaseClient.getInstance()?.appDatabase?.patrolActivityDao()

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
            getDataTemuanRequest
        }
    }

    fun insertDataTemuan(temuan: List<Temuan>?) {
        runnerScope.launch {
            temuanDao?.insertAll(temuan as List<Temuan>)
            temuan?.forEach {
                patrolDataDao?.flagObjectAsTemuan(it.object_id)
            }
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

    fun getDataTemuanAPI(): LiveData<List<Temuan>> {
        return getDataTemuanRequest
    }

    fun getTemuan(): LiveData<List<Temuan>>? {
        return temuanDao?.all
    }

    fun getTemuanByCheckPoint(checkpointId: String): LiveData<List<Temuan>>? {
        return temuanDao?.getByCheckpoint(checkpointId)
    }


    fun getZone(): LiveData<List<Zone>>? {
        return zoneDao?.zones
    }

    fun getCheckpointByZone(zoneId: String): LiveData<List<Checkpoint>>? {
        return checkpointDao?.checkpointByZones(zoneId)
    }

    fun getAllCheckpoint(): LiveData<List<Checkpoint>>? {
        return checkpointDao?.all
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

    fun addReportNormalDetail(dataReportDetail: ReportDetail) {
        runnerScope.launch {
            patrolDataDao?.insertReportDetail(dataReportDetail)
            patrolDataDao?.flagObjectAsNormal(dataReportDetail.admisecsgp_mstobj_objek_id)
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

    fun syncReport(report: Report, reportDetail: List<ReportDetail>?) {
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
        return unSyncReportList
    }

    fun getReportDetail(): LiveData<List<ReportDetailObject>>? {
        return patrolDataDao!!.getReportDetail()
    }

    fun setPatrolActivity(patrolActivity: PatrolActivity) {

        val map: MutableMap<String, RequestBody> = HashMap()
        map["id_jadwal_patroli"] =
            toRequestBody(patrolActivity.id_jadwal_patroli) as RequestBody
        map["status"] =
            toRequestBody(patrolActivity.status) as RequestBody
        map["start_at"] =
            toRequestBody(patrolActivity.start_at) as RequestBody
        if (patrolActivity.end_at != null) {

            map["end_at"] =
                toRequestBody(patrolActivity.end_at) as RequestBody
        }
        val restInterface = ServiceGenerator.createService()
        val post = restInterface.setPatrolActivity(map)
        post.enqueue(object : Callback<PatrolActivity> {
            override fun onResponse(
                call: Call<PatrolActivity>,
                response: Response<PatrolActivity>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        runnerScope.launch {
                            patrolActivityDao?.insert(response.body())
                        }
                    }
                } else {
                    Log.d("PatrolActivity", "PatrolActivity failed")
                }
            }

            override fun onFailure(call: Call<PatrolActivity>, t: Throwable) {
                Log.d("PatrolActivity", "Error " + t.message)
                t.printStackTrace()
            }
        })

    }

    fun setPatrolActivityDone(idJadwal: String) {
        runnerScope.launch {
            patrolActivityDao?.setActivityDone(idJadwal, Utils.createdAt())
            val patrolActivity: PatrolActivity? =
                patrolActivityDao?.getPatrolActivityByJadwal(idJadwal)

            if (patrolActivity != null) {
                patrolActivity.status = 1
                patrolActivity.end_at = Utils.createdAt()
                setPatrolActivity(patrolActivity)
            }
        }
    }

    fun setPatrolActivityStart(idJadwal: String) {
        runnerScope.launch {
            var patrolActivity: PatrolActivity? =
                patrolActivityDao?.getPatrolActivityByJadwal(idJadwal)
            if (patrolActivity != null) {
                patrolActivity.status = 0
            } else {
                patrolActivity = PatrolActivity(
                    id_jadwal_patroli = idJadwal,
                    start_at = Utils.createdAt(),
                    status = 0 //set on running
                )
            }
            setPatrolActivity(patrolActivity)
        }
    }

    fun insertPatrolActivity(patrolActivity: PatrolActivity) {
        runnerScope.launch {
            patrolActivityDao?.insert(patrolActivity)
//            patrolDataDao?.flagObjectAsTemuan(dataReportDetail.admisecsgp_mstobj_objek_id)
        }
    }

    fun getPatrolActivity(idJadwal: String): LiveData<PatrolActivity> {
        return patrolActivityDao!!.getPatrolActivity(idJadwal)
    }

    fun getPatrolActivityApi(idJadwal: String): LiveData<PatrolActivity> {
        val restInterface = ServiceGenerator.createService()
        val call = restInterface.getPatrolActivity(idJadwal = idJadwal)
        call!!.enqueue(object : Callback<PatrolActivity> {
            override fun onResponse(
                call: Call<PatrolActivity>,
                response: Response<PatrolActivity>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.id_jadwal_patroli != null) {
                            insertPatrolActivity(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PatrolActivity>, t: Throwable) {
                Log.d("PatrolActivity", "FAIL....", t)

            }
        })
        return getPatrolActivity(idJadwal)
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

    private val getDataTemuanRequest: LiveData<List<Temuan>>
        get() {
            val restInterface = ServiceGenerator.createService()
            val temuanRest = restInterface.getDataTemuan()
            temuanRest!!.enqueue(object : Callback<List<Temuan>> {
                override fun onResponse(
                    call: Call<List<Temuan>>,
                    response: Response<List<Temuan>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.isNotEmpty() == true) {
                            insertDataTemuan(response.body())
                        }
                    }
                }

                override fun onFailure(call: Call<List<Temuan>>, t: Throwable) {
                    Log.d("ZONE", "FAIL....", t)

                }
            })
            return temuanDao?.all!!
        }

//    private val


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