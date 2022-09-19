package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Zone
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ZoneDao {
    @get:Query("SELECT * FROM zone limit 1")
    val zone: LiveData<Zone>

    @get:Query("SELECT * FROM zone")
    val zones: LiveData<List<Zone>>

    @Insert
    suspend fun insert(zone: Zone?)

    @Delete
    fun delete(zone: Zone)

    @Query("DELETE FROM zone")
    fun deleteAll()
}