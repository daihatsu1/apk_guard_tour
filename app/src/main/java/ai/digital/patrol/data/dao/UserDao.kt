package ai.digital.patrol.data.dao

import ai.digital.patrol.data.entity.User
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Transaction
    suspend fun setLoggedInUser(loggedInUser: User) {
        deleteAll()
        insert(loggedInUser)
    }

    @get:Query("SELECT * FROM user limit 1")
    val user: LiveData<User>

    @get:Query("SELECT * FROM user")
    val getUser: User

    @Insert
    suspend fun insert(user: User?)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user")
    fun deleteAll()

}