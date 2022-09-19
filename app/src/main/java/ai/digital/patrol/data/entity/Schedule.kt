/*
 *     Digital Patrol Guard
 *     Schedule.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "schedule")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "shift") @SerializedName("shift") val shift: String? = null,
    @ColumnInfo(name = "shift_id") @SerializedName("shift_id") val shift_id: String? = null,
    @ColumnInfo(name = "plant_id") @SerializedName("plant_id") val plant_id: String? = null,
    @ColumnInfo(name = "plant_name") @SerializedName("plant_name") val plant_name: String? = null,
    @ColumnInfo(name = "jam_masuk") @SerializedName("jam_masuk") var jam_masuk: String? = null,
    @ColumnInfo(name = "jam_pulang") @SerializedName("jam_pulang") var jam_pulang: String? = null,
    @ColumnInfo(name = "date") @SerializedName("date") val date: String? = null,
)