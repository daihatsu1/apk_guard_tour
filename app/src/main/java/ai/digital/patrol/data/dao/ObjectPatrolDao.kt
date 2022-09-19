package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.ObjectPatrol
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ObjectPatrolDao {
    @get:Query("SELECT * FROM objectPatrol limit 1")
    val objectPatrol: LiveData<ObjectPatrol>

    @Insert
    suspend fun insert(objectPatrol: ObjectPatrol?)

    @Delete
    fun delete(objectPatrol: ObjectPatrol)

    @Query("DELETE FROM objectPatrol")
    fun deleteAll()

    @Query("SELECT * FROM objectPatrol where checkpoint_id = :checkpointId ")
    fun getObjectByCheckpoint(checkpointId: String): LiveData<List<ObjectPatrol>>?
}