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
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "temuan",
    indices = [Index(value = ["object_id", "checkpoint_id"], unique = true)]
)
data class Temuan(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "object_id") @SerializedName("object_id") val object_id: String,
    @ColumnInfo(name = "checkpoint_id") @SerializedName("chekpoint_id") val checkpoint_id: String,
    @ColumnInfo(name = "object_name") @SerializedName("nama_objek") val object_name: String,
    @ColumnInfo(name = "shift") @SerializedName("nama_shift") val shift: String? = null,
    @ColumnInfo(name = "shift_id") @SerializedName("shift_id") val shift_id: String? = null,
    @ColumnInfo(name = "description") @SerializedName("description") var description: String? = null,
    @ColumnInfo(name = "image") @SerializedName("image") var image: String? = null,
    @ColumnInfo(name = "date_patrol") @SerializedName("date_patroli") val date_patrol: String? = null,
)