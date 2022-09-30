package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.helper.Cons.PATROL_TIME_END
import ai.digital.patrol.helper.Cons.PATROL_TIME_START
import ai.digital.patrol.helper.Utils
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDao {
    @get:Query("SELECT * FROM schedule order by id asc limit 1 ")
    val get: LiveData<Schedule>

    @get:Query("SELECT * FROM schedule order by id asc limit 1 ")
    val current:Schedule


    @get:Query("SELECT * FROM schedule")
    val all: LiveData<List<Schedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(schedule: List<Schedule?>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule?)

    @Delete
    fun delete(schedule: Schedule)

    @Query("DELETE FROM schedule")
    fun deleteAll()


}