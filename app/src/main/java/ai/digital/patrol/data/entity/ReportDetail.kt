/*
 *     Digital Patrol Guard
 *     Report.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:37 AM
 */

package ai.digital.patrol.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(
    tableName = "report_detail", foreignKeys = [
        ForeignKey(
            entity = Report::class,
            onUpdate = ForeignKey.CASCADE,
            parentColumns = arrayOf("sync_token"),
            childColumns = arrayOf("report")
        ),
        ForeignKey(
            entity = ObjectPatrol::class,
            onUpdate = ForeignKey.CASCADE,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("admisecsgp_mstobj_objek_id")
        )
    ]
)
data class ReportDetail(
    @PrimaryKey @SerializedName("sync_token") @ColumnInfo(name = "sync_token") var sync_token: String = UUID.randomUUID()
        .toString(),
    @ColumnInfo(name = "trans_detail_id") @SerializedName("trans_detail_id") val trans_detail_id: Int? = null,
    @ColumnInfo(name = "admisecsgp_trans_headers_trans_headers_id") @SerializedName("admisecsgp_trans_headers_trans_headers_id") var admisecsgp_trans_headers_trans_headers_id: Int? = null,
    @ColumnInfo(name = "admisecsgp_mstobj_objek_id") @SerializedName("admisecsgp_mstobj_objek_id") val admisecsgp_mstobj_objek_id: String? = null,
    @ColumnInfo(name = "conditions") @SerializedName("conditions") val conditions: String? = null,
    @ColumnInfo(name = "admisecsgp_mstevent_event_id") @SerializedName("admisecsgp_mstevent_event_id") val admisecsgp_mstevent_event_id: String? = null,
    @ColumnInfo(name = "description") @SerializedName("description") val description: String? = null,
    @ColumnInfo(name = "image_1") @SerializedName("image_1") val image_1: String? = null,
    @ColumnInfo(name = "image_2") @SerializedName("image_2") val image_2: String? = null,
    @ColumnInfo(name = "image_3") @SerializedName("image_3") val image_3: String? = null,
    @ColumnInfo(name = "is_laporan_kejadian") @SerializedName("is_laporan_kejadian") val is_laporan_kejadian: Int? = null,
    @ColumnInfo(name = "laporkan_pic") @SerializedName("laporkan_pic") val laporkan_pic: Int? = null,
    @ColumnInfo(name = "is_tindakan_cepat") @SerializedName("is_tindakan_cepat") val is_tindakan_cepat: Int? = null,
    @ColumnInfo(name = "deskripsi_tindakan") @SerializedName("deskripsi_tindakan") val deskripsi_tindakan: String? = null,
    @ColumnInfo(name = "note_tindakan_cepat") @SerializedName("note_tindakan_cepat") val note_tindakan_cepat: String? = null,
    @ColumnInfo(name = "status") @SerializedName("status") val status: Int = 1,
    @ColumnInfo(name = "created_at") @SerializedName("created_at") val created_at: String? = null,
    @ColumnInfo(name = "synced") var synced: Boolean? = false,
    @ColumnInfo(name = "report") var reportId: String,
) {
}
