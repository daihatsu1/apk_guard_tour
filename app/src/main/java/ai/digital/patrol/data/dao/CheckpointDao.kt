package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.Zone
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CheckpointDao {
    @get:Query("SELECT * FROM checkpoint limit 1")
    val checkpoint: LiveData<Checkpoint>

    @Query("SELECT * FROM checkpoint where zone_id = :zone_id ")
    fun checkpointByZones(zone_id:String): LiveData<List<Checkpoint>>

    @Insert
    suspend fun insert(checkpoint: Checkpoint?)

    @Delete
    fun delete(checkpoint: Checkpoint)

    @Query("DELETE FROM checkpoint")
    fun deleteAll()
}