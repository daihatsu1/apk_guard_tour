package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.Zone
import androidx.room.*


@Dao
interface ZoneCheckpointDao {

//    @get:Query("SELECT * FROM survey_data ORDER BY created_at DESC")
//    val all: LiveData<List<SurveyData>>
//
//    @Query("SELECT * FROM survey_data WHERE synced=0 ORDER BY created_at DESC")
//    fun getUnSyncSurveyData(): List<SurveyDataDetail>
//
//    @Query("SELECT * FROM survey_data ORDER BY created_at DESC")
//    fun getDataDetail(): LiveData<List<SurveyDataDetail>>
//
//    @Query("SELECT * FROM survey_data ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
//    suspend fun paginateDataDetail(limit:Int, offset:Int): List<SurveyDataDetail>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(surveyData: List<SurveyData>)
//

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertZone(zone: Zone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCheckpoint(checkpoint: List<Checkpoint>)

    @Transaction
    fun insertZoneCheckpoint(zone: Zone, checkpoint: List<Checkpoint>?) {
        insertZone(zone)
        if (checkpoint != null) {
            checkpoint.forEach { it.zone_id = zone.id }
            insertCheckpoint(checkpoint)
        }
    }
//    @Delete
//    fun delete(surveyData: SurveyData)

}