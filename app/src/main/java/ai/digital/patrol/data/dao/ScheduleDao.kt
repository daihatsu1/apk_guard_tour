package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.entity.Zone
import ai.digital.patrol.helper.Cons.PATROL_TIME_END
import ai.digital.patrol.helper.Cons.PATROL_TIME_START
import ai.digital.patrol.helper.Utils
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDao {
    @get:Query("SELECT * FROM schedule order by id asc limit 1 ")
    val get: LiveData<Schedule>


    @get:Query("SELECT * FROM schedule")
    val all: LiveData<List<Schedule>>

    @Insert
    suspend fun insertList(schedule: List<Schedule?>) {
        schedule.forEach {
            if (it != null) {
                if (it.shift!="LIBUR"){
                    it.jam_masuk =
                        it.jam_masuk?.let { it1 -> Utils.formatScheduleTime(it1, PATROL_TIME_START) }
                    it.jam_pulang =
                        it.jam_pulang?.let { it1 -> Utils.formatScheduleTime(it1, PATROL_TIME_END) }
                }
                insert(it)
            }
        }
    }

    @Insert
    suspend fun insert(schedule: Schedule?)

    @Delete
    fun delete(schedule: Schedule)

    @Query("DELETE FROM schedule")
    fun deleteAll()


}