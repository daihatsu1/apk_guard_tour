package ai.digital.patrol

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DatabaseClient private constructor() {

    //our app database object
    private val mCtx: Context = GuardTourApplication.applicationContext()
    val appDatabase: AppDatabase =
        Room.databaseBuilder(mCtx, AppDatabase::class.java, "GuardTourDB")
            .allowMainThreadQueries()
            .build()

    @DelicateCoroutinesApi
    fun clearTables() {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.clearAllTables()
        }
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var mInstance: DatabaseClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): DatabaseClient? {
            if (mInstance == null) {
                mInstance = DatabaseClient()
            }
            return mInstance
        }
    }
}