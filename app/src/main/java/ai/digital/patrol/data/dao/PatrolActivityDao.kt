package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.PatrolActivity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PatrolActivityDao {
    @Query("SELECT * FROM patrol_activity where id_jadwal_patroli=:idJadwal order by id asc limit 1 ")
    fun getPatrolActivity(idJadwal: String): LiveData<PatrolActivity>

    @Query("SELECT * FROM patrol_activity where id_jadwal_patroli=:idJadwal order by id asc limit 1 ")
    fun getPatrolActivityByJadwal(idJadwal: String): PatrolActivity

    @get:Query("SELECT * FROM patrol_activity")
    val all: LiveData<List<PatrolActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(patrol_activity: List<PatrolActivity?>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patrol_activity: PatrolActivity?)

    @Delete
    fun delete(patrol_activity: PatrolActivity)

    @Query("DELETE FROM patrol_activity")
    fun deleteAll()

    @Query("UPDATE patrol_activity SET status=1, end_at=:end_at WHERE id_jadwal_patroli = :idJadwal")
    fun setActivityDone(idJadwal: String, end_at: String)
    fun setActivityStart(idJadwal: String, createdAt: String) {

    }


}