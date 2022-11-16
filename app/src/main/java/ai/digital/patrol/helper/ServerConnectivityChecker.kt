/*
 *     Digital Patrol Guard
 *     connectivityChecker.kt
 *     Created by ImamSyahrudin on 17/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/17/22, 12:45 AM
 */

package ai.digital.patrol.helper

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData


class ServerConnectivityChecker constructor(private val context: Context){

    // forever check
    inner class ObservableNetworkCondition: LiveData<Boolean>() {

        override fun onActive() {
            super.onActive()

        }

        override fun onInactive() {
            super.onInactive()

        }


    }

    // on execution check
    fun isNetworkConnected(): Boolean {

        return true
    }
}