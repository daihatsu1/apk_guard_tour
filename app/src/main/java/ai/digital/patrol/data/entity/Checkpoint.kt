/*
 *     Digital Patrol Guard
 *     Checkpoint.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 10:40 PM
 */

package ai.digital.patrol.data.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "checkpoint", foreignKeys = (arrayOf(
        ForeignKey(
            entity = Zone::class,
            onUpdate = ForeignKey.NO_ACTION,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("zone_id")
        )
    )),
    indices= [Index("id", unique = true), Index("zone_id")],
    )
@Parcelize
data class Checkpoint(
    @PrimaryKey(autoGenerate = true) val checkpointLocalId: Int,
    @ColumnInfo(name = "id") @SerializedName("id") var id: String,
    @ColumnInfo(name = "zone_id") @SerializedName("zone_id")var zone_id: String? = null,
    @ColumnInfo(name = "check_name") @SerializedName("check_name")val check_name: String? = null,
    @ColumnInfo(name = "no_nfc") @SerializedName("no_nfc")val no_nfc: String? = null,
    @ColumnInfo(name = "status_checkpoint") @SerializedName("status") val status_checkpoint: String? = null,
    @ColumnInfo(name = "patrol_status") val patrol_status: Boolean? = null,
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    @SerializedName("objects")
    @ColumnInfo(name = "objects")
    val objects: List<ObjectPatrol>? = null
}
