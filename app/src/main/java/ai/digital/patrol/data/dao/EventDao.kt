package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.Event
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @get:Query("SELECT * FROM event limit 1")
    val event: LiveData<Event>

    @Insert
    suspend fun insert(event: Event?)

    @Delete
    fun delete(event: Event)

    @Query("DELETE FROM event")
    fun deleteAll()

    @Query("SELECT * FROM event where object_id = :objectId limit 1")
    fun getEventByObjectId(objectId: String): LiveData<List<Event>>
}