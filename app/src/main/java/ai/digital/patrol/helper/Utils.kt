/*
 *     Digital Patrol Guard
 *     Utils.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.digital.patrol.helper

import ai.digital.patrol.BuildConfig
import ai.digital.patrol.R
import ai.digital.patrol.callback.OnInternetConnected
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object Utils {
    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location?): String {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun urlValidations(url: String): String {
        if (BuildConfig.DEBUG)
            return url.replace("localhost", "10.0.2.2")
        return url
    }

    enum class TypePhoto {
        ASSET_PHOTO, ASSET_AREA_PHOTO, ASSET_STREET_PHOTO
    }

    fun getTypePhotoId(typePhoto: TypePhoto): Int {
        return when (typePhoto) {
            TypePhoto.ASSET_PHOTO -> {
                1
            }

            TypePhoto.ASSET_AREA_PHOTO -> {
                2
            }

            TypePhoto.ASSET_STREET_PHOTO -> {
                3
            }
        }
    }


    fun encoder(filePath: String): String {
        val bytes = File(filePath).readBytes()
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }

    fun decoder(base64Str: String): Bitmap? {
        val imageByteArray = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    fun formatDate(
        dateStr: String,
        currentFormat: String = "dd-MM-yyyy",
        newFormat: String = "dd-MM-yyyy"
    ): String {
        val inputFormat: DateFormat = SimpleDateFormat(currentFormat, Locale("ID"))
        val outputFormat: DateFormat = SimpleDateFormat(newFormat, Locale("ID"))
        val date: Date = inputFormat.parse(dateStr) as Date

        return outputFormat.format(date)
    }

    fun scheduleDateFormat(dateStr: String): String? {
        val inputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("ID"))
        val outputFormat: DateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ID"))

        val date: Date = inputFormat.parse(dateStr) as Date

        return outputFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun timeFormat(timeStr: String, format: String): Date {
        val inputFormat: DateFormat = SimpleDateFormat(format, Locale("ID"))
        return inputFormat.parse(timeStr) as Date
    }

    fun formatScheduleTime(timeStr: String, minute: Int): String {
        val timeObj = timeFormat(timeStr, "HH:mm:ss")
        val df = SimpleDateFormat("HH:mm", Locale("ID"))
        val calStart = Calendar.getInstance()
        calStart.time = timeObj
        calStart.add(Calendar.MINUTE, minute)
        return df.format(calStart.time)
    }

    fun isOnPatrolTime(datePatrol: String?, jamMasuk: String?, jamPulang: String?): Boolean {
        if (datePatrol != null && jamMasuk != null && jamPulang != null) {
            val currentTime = Calendar.getInstance().time
            val now = createdAt("yyyy-MM-dd")
            val s = "$datePatrol $jamMasuk"
            val e = "$datePatrol $jamPulang"

            val startTime = timeFormat(s, "dd-MM-yyyy HH:mm")
            var endTime = timeFormat(e, "dd-MM-yyyy HH:mm")

            if (endTime.before(startTime)) {
                val c = Calendar.getInstance()
                c.time = endTime
                c.add(Calendar.DATE, 1)
                endTime = c.time
            }
            if (currentTime.after(startTime) && currentTime.before(endTime)) {
                return true
            }
        }
        return false
    }

    fun formatTimePatrol(timeStr: String, tomorrow: Boolean): Date {
        val current = LocalDateTime.now()
        val time = timeFormat(timeStr, "HH:mm")

        val cal = Calendar.getInstance()
        cal.time = time
        cal.set(Calendar.YEAR, current.year)
        cal.set(Calendar.MONTH, current.monthValue)
        cal.set(Calendar.DATE, current.dayOfMonth)
        if (tomorrow) {
            cal.add(Calendar.DATE, 1)
        }

        return cal.time
    }

    fun formatTextTimePatrol(jamMasuk: String, jamPulang: String): String {
        val current = LocalDateTime.now()

        val startTime = timeFormat(jamMasuk, "HH:mm")

        val calStart = Calendar.getInstance()
        calStart.time = startTime
        calStart.set(Calendar.YEAR, current.year)
        calStart.set(Calendar.MONTH, current.monthValue)
        calStart.set(Calendar.DATE, current.dayOfMonth)

        val endTime = timeFormat(jamPulang, "HH:mm")
        val calEnd = Calendar.getInstance()

        calEnd.time = endTime
        calEnd.set(Calendar.YEAR, current.year)
        calEnd.set(Calendar.MONTH, current.monthValue)
        calEnd.set(Calendar.DATE, current.dayOfMonth)

        if (endTime.before(startTime)) {
            calEnd.add(Calendar.DATE, 1)
        }

        val df = SimpleDateFormat("HH:mm", Locale("ID"))
        val newStartTime = df.format(calStart.time)
        val newEndTime = df.format(calEnd.time)

        return "$newStartTime - $newEndTime"
    }

    @SuppressLint("SimpleDateFormat")
    fun createdAt(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(format)
            current.format(formatter)
        } else {
            val date = Date()
            val formatter = SimpleDateFormat(format)
            formatter.format(date)
        }
    }

    private fun blinkAnim(): Animation {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 90 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        return anim
    }

    fun connectionChecker(
        owner: LifecycleOwner,
        applicationContext: Context,
        view: View,
        listener: OnInternetConnected?
    ) {
        val networkConnection = ConnectivityChecker(applicationContext)

        // Forever Checking example
        networkConnection.ObservableNetworkCondition()
            .observe(owner) {
                if (it) {
                    view.setBackgroundResource(R.color.success)
                    listener?.onConnected()
                } else {
                    view.setBackgroundResource(R.color.alert)
                }
                view.animation = blinkAnim()
            }

        if (networkConnection.isNetworkConnected()) {
            // do something when device connected to internet
            Log.d("INTERNET_CHECKER", "true")
            view.setBackgroundResource(R.color.success)

        } else {
            // do something when device disconnected to internet
            Log.d("INTERNET_CHECKER", "false")
            view.setBackgroundResource(R.color.alert)
        }
        view.animation = blinkAnim()
    }

    fun toRequestBody(value: Any?): Any {
        return RequestBody.create(MediaType.parse("text/plain"), value.toString())
    }


    fun setConfig(context: Context) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val viewMode = prefs.getBoolean(Cons.VIEW_MODE, false)
        if (viewMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}
