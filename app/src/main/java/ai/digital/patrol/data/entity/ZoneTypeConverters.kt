/*
 *     Digital Patrol Guard
 *     ZoneTypeConverters.kt
 *     Created by ImamSyahrudin on 13/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/13/22, 12:27 PM
 */
package ai.digital.patrol.data.entity

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts non-standard objects in the {@link MyLocation} data class into and out of the database.
 */
class ZoneTypeConverters {
    @SuppressLint("SimpleDateFormat")
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun fromDate(date: Date): String {
        val df: DateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // Quoted "Z" to indicate UTC, no timezone offset
        return df.format(date).toString()
    }

    @TypeConverter
    fun toDate(date: String): Date? {
        return format.parse(date)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
}
