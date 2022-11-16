/*
 *     Digital Patrol Guard
 *     LoginRepository.kt
 *     Created by ImamSyahrudin on 13/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/13/22, 2:02 PM
 */

package ai.digital.patrol.data.repository

import ai.digital.patrol.DatabaseClient
import ai.digital.patrol.data.dao.*
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.entity.Shift
import ai.digital.patrol.networking.ServiceGenerator
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


class ScheduleRepository(val dataSource: DatabaseClient) {

    // in-memory cache of the user object
    internal var schedule: Schedule? = null
    internal var shift: Shift? = null

    private val scheduleDao: ScheduleDao? = DatabaseClient.getInstance()?.appDatabase?.scheduleDao()
    private val shiftDao: ShiftDao? = DatabaseClient.getInstance()?.appDatabase?.shiftDao()


    private val runnerScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext =
            Dispatchers.IO // no job added i.e + SupervisorJob()
    }

    init {
        schedule = null

    }

    fun insertSchedule(schedule: List<Schedule?>) {
        runnerScope.launch {
            scheduleDao?.deleteAll()
            scheduleDao?.insertList(schedule)
        }
    }

    fun insertShift(shift: List<Shift?>) {
        runnerScope.launch {
            shiftDao?.insertList(shift)
        }
    }

    fun getSchedule(): LiveData<Schedule>? {
        return scheduleDao?.get
    }


    fun getScheduleAPI(): LiveData<List<Schedule>>? {
        return scheduleRequest
    }

    fun getShiftAPI(): LiveData<List<Shift>>? {
        return shiftRequest
    }

    fun getCurrentShift(): LiveData<Shift>? {
        return shiftDao?.current
    }
    fun getPatrolShift(): LiveData<Shift>? {
        return shiftDao?.patrolShift
    }

    fun getCurrentSchedule(): Schedule? {
        return scheduleDao?.current
    }

    private val scheduleRequest: LiveData<List<Schedule>>?
        get() {
            val restInterface = ServiceGenerator.createService()
            val scheduleDataCall = restInterface.getJadwal()
            scheduleDataCall!!.enqueue(object : Callback<List<Schedule?>> {
                override fun onResponse(
                    call: Call<List<Schedule?>>,
                    response: Response<List<Schedule?>>
                ) {
                    if (response.isSuccessful) {

                        val schedules = response.body()
                        if (schedules?.isEmpty() == false) {
                            if (schedules[0] != null) {
                                insertSchedule(schedules)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<Schedule?>>, t: Throwable) {
                    Log.d("SCHEDULE", "FAIL....", t)
                }
            })
            return scheduleDao?.all
        }
    private val shiftRequest: LiveData<List<Shift>>?
        get() {
            val restInterface = ServiceGenerator.createService()
            val scheduleDataCall = restInterface.getShift()
            scheduleDataCall!!.enqueue(object : Callback<List<Shift?>> {
                override fun onResponse(
                    call: Call<List<Shift?>>,
                    response: Response<List<Shift?>>
                ) {
                    if (response.isSuccessful) {

                        val shift = response.body()
                        if (shift?.isEmpty() == false) {
                            if (shift[0] != null) {
                                insertShift(shift)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<Shift?>>, t: Throwable) {
                    Log.d("Shift", "FAIL....", t)
                }
            })
            return shiftDao?.all
        }

    companion object {
        @Volatile
        private var instance: ScheduleRepository? = null

        @JvmStatic
        fun getInstance(): ScheduleRepository? {
            if (instance == null) {
                instance = ScheduleRepository(DatabaseClient.getInstance()!!)
            }
            return instance
        }
    }

}