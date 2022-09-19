/*
 *     Digital Patrol Guard
 *     AppProvider.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import android.util.Log

class GuardTourApplication: Application(), Configuration.Provider {
    init {
        instance = this
    }

    companion object {
        private var instance: GuardTourApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        // initialize for any

        // Use ApplicationContext.
        // example: SharedPreferences etc...
        applicationContext()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }
}