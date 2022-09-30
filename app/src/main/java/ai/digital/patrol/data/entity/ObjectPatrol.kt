/*
 *     Digital Patrol Guard
 *     ObjectPatrol.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:01 AM
 */

package ai.digital.patrol.data.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "objectPatrol",
    foreignKeys = (arrayOf(
        ForeignKey(
            entity = Checkpoint::class,
            onUpdate = ForeignKey.NO_ACTION,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("checkpoint_id")
        )
    )),
    indices = [androidx.room.Index("id", unique = true)],
    )
@Parcelize
data class ObjectPatrol(
    @PrimaryKey(autoGenerate = true) val objectLocalId: Int,
    @ColumnInfo(name = "id") @SerializedName("id") val id: String,
    @ColumnInfo(name = "nama_objek") @SerializedName("nama_objek") val nama_objek: String? = null,
    @ColumnInfo(name = "checkpoint_id") @SerializedName("checkpoint_id") var checkpoint_id: String? = null,
    @ColumnInfo(name = "status_object") @SerializedName("status") var status_object: Int = 0,
    // state object
    @ColumnInfo(name = "is_normal") val is_normal: Boolean? = null,
    // default is null,
    // true when at confirm dialog click normal,
    // false when abnormal

    ) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    @SerializedName("event")
    @ColumnInfo(name = "event")
    val event: List<Event>? = null
}
