/*
 *     Digital Patrol Guard
 *     ScheduleVIewModel.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.main

import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.repository.ScheduleRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScheduleViewModel(application) as T
        }
    }

    private val repository = ScheduleRepository.getInstance()

    fun getSchedules(): LiveData<List<Schedule>>? {
        return repository!!.getScheduleAPI()
    }
    fun getSchedule(): LiveData<Schedule>? {
        return repository!!.getSchedule()
    }

    fun getScheduleOnly(): Schedule? {
        return repository!!.getCurrentSchedule()

    }
}