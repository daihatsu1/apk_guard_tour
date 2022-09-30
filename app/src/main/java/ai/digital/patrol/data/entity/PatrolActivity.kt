/*
 *     Digital Patrol Guard
 *     PatrolActivity.kt
 *     Created by ImamSyahrudin on 26/9/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 9/26/22, 1:34 AM
 */

package ai.digital.patrol.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "patrol_activity",
    indices = [Index(value = ["id_jadwal_patroli"], unique = true)]
)
data class PatrolActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "activity_id") @SerializedName("activity_id") val activity_id: Int? = null,
    @ColumnInfo(name = "id_jadwal_patroli") @SerializedName("admisecsgp_trans_jadwal_patroli_id_jadwal_patroli") val id_jadwal_patroli: String,
    @ColumnInfo(name = "status") @SerializedName("status") var status: Int? = null,
    @ColumnInfo(name = "start_at") @SerializedName("start_at") val start_at: String? = null,
    @ColumnInfo(name = "end_at") @SerializedName("end_at") var end_at: String? = null,
) : Serializable