/*
 *     Digital Patrol Guard
 *     RuntimePermissions.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat


/**
 * The Runtime permissions helper class
 */
class RuntimePermissions(private val permissions: Array<String>?, context: Context?) {
    private val mContext: Context? = context
    fun hasPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    init {
        val PERMISSION_ALL = 1
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                (mContext as Activity?)!!,
                permissions!!, PERMISSION_ALL
            )
        }
    }
}