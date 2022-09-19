package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.ObjectPatrol
import androidx.room.*


@Dao
interface CheckpointObjectDao {

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
    fun insertCheckpoint(checkpoint: Checkpoint)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObject(objectPatrol: List<ObjectPatrol>?)

    @Transaction
    fun insertCheckpointObject(checkpoint: Checkpoint, objectPatrol: List<ObjectPatrol>?) {
        insertCheckpoint(checkpoint)
        if (objectPatrol != null) {
            objectPatrol.forEach { it.checkpoint_id = checkpoint.id }
            insertObject(objectPatrol)
        }
    }
//    @Delete
//    fun delete(surveyData: SurveyData)

}