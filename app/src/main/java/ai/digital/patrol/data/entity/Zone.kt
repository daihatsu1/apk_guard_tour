/*
 *     Digital Patrol Guard
 *     Zone.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.data.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "zone", indices= [Index("id", unique = true)],
)
@Parcelize
data class Zone(
    @PrimaryKey(autoGenerate = true) val zoneLocalId:Int,
    @ColumnInfo(name = "id") @SerializedName("id") var id: String,
    @ColumnInfo(name = "plant_id") @SerializedName("plant_id") val plant_id: String? = null,
    @ColumnInfo(name = "plant_name") @SerializedName("plant_name") val plant_name: String? = null,
    @ColumnInfo(name = "zone_name") @SerializedName("zone_name") val zone_name: String? = null,
    @ColumnInfo(name = "nama_shift") @SerializedName("nama_shift") val nama_shift: String? = null,
    @ColumnInfo(name = "patrol_status") val patrol_status: Boolean? = null,
    ) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    @SerializedName("checkpoints")
    val checkpoints: List<Checkpoint>?=null
}