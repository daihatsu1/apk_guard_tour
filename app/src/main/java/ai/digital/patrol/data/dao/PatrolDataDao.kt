package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.*
import ai.digital.patrol.helper.Utils
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface PatrolDataDao {

    @Query("SELECT * FROM zone ")
    fun getData(): LiveData<List<Zone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertZone(zone: Zone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCheckpoints(checkpoint: List<Checkpoint>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCheckpoint(checkpoint: Checkpoint?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObjects(objectPatrol: ObjectPatrol)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event)

    fun insertPatrolDate(zones: List<Zone?>) {
        zones.forEach {
            if (it != null) {
                insertZone(it)
                val checkpoint = it.checkpoints
                checkpoint?.forEach { _checkpoint ->
                    insertCheckpoint(_checkpoint)
                    val objectPatrols = _checkpoint.objects
                    objectPatrols?.forEach { _objectPatrol ->
                        insertObjects(_objectPatrol)
                        val event = _objectPatrol.event
                        event?.forEach { _event ->
                            insertEvent(_event)
                        }
                    }
                }
            }
        }
    }

    @Transaction
    fun insertZoneCheckpoint(zone: Zone, checkpoint: List<Checkpoint>?) {
        deleteAllEvent()
        deleteAllObjectPatrol()
        deleteAllCheckpoint()
        deleteAllZone()
        insertZone(zone)
        checkpoint?.forEach {
            it.zone_id = zone.id
            insertCheckpointObject(it)
        }
    }

    @Transaction
    fun insertCheckpointObject(checkpoint: Checkpoint) {
        insertCheckpoint(checkpoint)
        checkpoint.objects?.forEach {
            insertObjects(it)
        }
    }


    @Query("DELETE FROM zone")
    fun deleteAllZone()

    @Query("DELETE FROM checkpoint")
    fun deleteAllCheckpoint()

    @Query("DELETE FROM event")
    fun deleteAllEvent()


    @Query("DELETE FROM report")
    fun deleteAllReport()

    @Query("DELETE FROM report_detail")
    fun deleteAllReportDetail()

    @Query("DELETE FROM objectPatrol")
    fun deleteAllObjectPatrol()

    @Query("DELETE FROM schedule")
    fun deleteAllSchedule()

    @Query("DELETE FROM patrol_activity")
    fun deleteAllPatrolActivity()

    fun clearAll() {
        deleteAllSchedule()
        deleteAllPatrolActivity()
        deleteAllEvent()
        deleteAllObjectPatrol()
        deleteAllCheckpoint()
        deleteAllZone()
        deleteAllReportDetail()
        deleteAllReport()
    }

    @Query("UPDATE objectPatrol SET is_normal=0 WHERE id = :msObjectsId")
    fun flagObjectAsTemuan(msObjectsId: String?)

    @Query("UPDATE objectPatrol SET is_normal=1 WHERE id = :msObjectsId")
    fun flagObjectAsNormal(msObjectsId: String?)

    @Query(
        "UPDATE report SET checkin_checkpoint=:checkin_at " +
                "WHERE admisecsgp_mstckp_checkpoint_id = :checkpointId and checkin_checkpoint not null"
    )
    fun checkInCheckpoint(checkin_at: String, checkpointId: String)

    @Query(
        "UPDATE report SET checkout_checkpoint=:checkout_at, synced=0 " +
                "WHERE admisecsgp_mstckp_checkpoint_id = :checkpointId"
    )
    fun checkOutCheckpoint(checkout_at: String, checkpointId: String)

    @Query("UPDATE zone SET patrol_status=1 WHERE id = :zoneId")
    fun setZoneOnPatrol(zoneId: String)

    @Query("UPDATE zone SET patrol_status=0 WHERE id = :zoneId")
    fun setZoneOnPatrolDone(zoneId: String)

    @Query("UPDATE checkpoint SET patrol_status=1 WHERE id = :checkpointId")
    fun setCheckpointOnPatrol(checkpointId: String)

    @Query("UPDATE checkpoint SET patrol_status=0 WHERE id = :checkpointId")
    fun setCheckpointDonePatrol(checkpointId: String)

    @Query("SELECT * FROM report where admisecsgp_mstckp_checkpoint_id = :checkpointId limit 1")
    fun checkReport(checkpointId: String): LiveData<Report>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReport(report: Report)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReportDetail(dataReportDetail: ReportDetail)

    fun insertReportWithDetail(report: Report, dataReportDetail: ReportDetail) {
//        insertReport(report)
        insertReportDetail(dataReportDetail)
    }

    @Query("select * from report_detail where sync_token=:sync_token")
    fun getReportDetail(sync_token: String): ReportDetail?


    @Query("select * from report where sync_token=:sync_token")
    fun getReport(sync_token: String): LiveData<Report>


    @Query("select * from report where sync_token=:sync_token")
    suspend fun getReportBySyncToken(sync_token: String): Report
    @Query("select * from report where admisecsgp_mstckp_checkpoint_id=:checkpointId")
    suspend fun getReportByCheckpointId(checkpointId: String): Report?

    @Query("select * from report where sync_token=:sync_token")
    fun getDataReport(sync_token: String):Report

    @Transaction
    fun insertDataReport(report: Report, reportDetail: List<ReportDetail>?) {
        val olderReport = getDataReport(report.sync_token)
        if (olderReport != null) {
            if (olderReport.checkout_checkpoint !=null && report.checkout_checkpoint==null){
                report.synced =false
                report.checkout_checkpoint =olderReport.checkout_checkpoint
            }
        }
        insertReport(report)
        reportDetail?.forEach {
            it.reportId = report.sync_token
            val oldDetail = getReportDetail(it.sync_token)
            it.synced = true

            it.image_1 = Utils.toNull(it.image_1)
            it.image_2 = Utils.toNull(it.image_2)
            it.image_3 = Utils.toNull(it.image_3)
            if (oldDetail != null) {
                oldDetail.image_1 =
                    if (oldDetail.image_1 == null) null else Utils.toNull(oldDetail.image_1)
                oldDetail.image_2 =
                    if (oldDetail.image_2 == null) null else Utils.toNull(oldDetail.image_2)
                oldDetail.image_3 =
                    if (oldDetail.image_3 == null) null else Utils.toNull(oldDetail.image_3)


                if (!oldDetail.image_1.isNullOrBlank() and it.image_1.isNullOrBlank()) {
                    it.image_1 = oldDetail.image_1
                    it.synced = false
                }
                if (!oldDetail.image_2.isNullOrBlank() and it.image_2.isNullOrBlank()) {
                    it.image_2 = oldDetail.image_2
                    it.synced = false
                }
                if (!oldDetail.image_3.isNullOrBlank() and it.image_3.isNullOrBlank()) {
                    it.image_3 = oldDetail.image_3
                    it.synced = false
                }
            }

            insertReportDetail(it)
        }
    }

    @Query(
        "SELECT r.* FROM report r left join report_detail rd on r.sync_token = rd.report " +
                "where r.synced != 1 or rd.synced != 1 order by status ASC"
    )
    fun getUnSyncReport(): List<ReportDataDetail>?

    @Query("SELECT * FROM report_detail where status=0 ORDER BY created_at DESC")
    fun getReportDetail(): LiveData<List<ReportDetailObject>>?

    @Query("DELETE FROM report where synced = 1")
    fun deleteAllNormalReport()

    @Query("DELETE FROM report_detail where synced=1")
    fun deleteAllNormalDetailReport()

    fun clearDataReport() {
        deleteAllNormalDetailReport()
        deleteAllNormalReport()
    }

}