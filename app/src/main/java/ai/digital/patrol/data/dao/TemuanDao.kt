package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.ReportDataDetail
import ai.digital.patrol.data.entity.Temuan
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TemuanDao {
    @get:Query("SELECT * FROM temuan order by id asc limit 1 ")
    val get: LiveData<Temuan>

    @get:Query("SELECT * FROM temuan")
    val all: LiveData<List<Temuan>>

    @Query("SELECT * FROM temuan where checkpoint_id = :checkpointId")
    fun getByCheckpoint(checkpointId: String): LiveData<List<Temuan>>?

    @Insert
    suspend fun insert(temuan: Temuan?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(temuan: List<Temuan>)

    @Delete
    fun delete(temuan: Temuan)

    @Query("DELETE FROM Temuan")
    fun deleteAll()


}