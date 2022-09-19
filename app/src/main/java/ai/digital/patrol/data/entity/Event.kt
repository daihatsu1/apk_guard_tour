/*
 *     Digital Patrol Guard
 *     Event.kt
 *     Created by ImamSyahrudin on 21/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/21/22, 12:10 PM
 */

package ai.digital.patrol.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "event",
    foreignKeys = (arrayOf(
        ForeignKey(
            entity = ObjectPatrol::class,
            onUpdate = ForeignKey.CASCADE,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("object_id")
        )
    )),
    indices = [androidx.room.Index("object_id", unique = true)],

    )
data class Event(
    @PrimaryKey(autoGenerate = true) @NonNull val eventLocalId: Int,
    @ColumnInfo(name = "id") @SerializedName("id") val id: String? = null,
    @ColumnInfo(name = "object_id") @SerializedName("object_id") val object_id: String? = null,
    @ColumnInfo(name = "event_name") @SerializedName("event_name") val event_name: String? = null,
)
