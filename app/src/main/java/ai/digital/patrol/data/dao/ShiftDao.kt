package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Shift
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShiftDao {
    @get:Query("SELECT * FROM shift order by shift_id asc limit 1 ")
    val get: LiveData<Shift>

    @get:Query("SELECT * FROM shift where  datetime('now', 'localtime') between jam_masuk and jam_pulang order by shift_id asc;")
    val current:Shift

    @get:Query("SELECT * FROM shift where  datetime('now', 'localtime') between jam_masuk and jam_pulang and status_patrol = 1 order by shift_id asc;")
    val patrolShift:Shift

    @get:Query("SELECT * FROM shift")
    val all: LiveData<List<Shift>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(shift: List<Shift?>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shift: Shift?)

    @Delete
    fun delete(shift: Shift)

    @Query("DELETE FROM shift")
    fun deleteAll()

    @Query("UPDATE shift SET status_patrol=:status WHERE shift_id = :currentShift")
    fun updateRunningPatrolShift(currentShift: String, status:String)


}