/*
 *     Digital Patrol Guard
 *     Report.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:37 AM
 */

package ai.digital.patrol.data.entity

import ai.digital.patrol.helper.Utils
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "report",
    indices = [androidx.room.Index("admisecsgp_mstckp_checkpoint_id", unique = true)],
)
@Parcelize
data class Report(
    @PrimaryKey @SerializedName("sync_token") @ColumnInfo(name = "sync_token") var sync_token: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "trans_header_id") @SerializedName("trans_header_id") val trans_header_id: Int? = null,
    @ColumnInfo(name = "admisecsgp_mstusr_npk") @SerializedName("admisecsgp_mstusr_npk") val admisecsgp_mstusr_npk: String?,
    @ColumnInfo(name = "admisecsgp_mstshift_shift_id") @SerializedName("admisecsgp_mstshift_shift_id") val admisecsgp_mstshift_shift_id: String,
    @ColumnInfo(name = "admisecsgp_mstzone_zone_id") @SerializedName("admisecsgp_mstzone_zone_id") val admisecsgp_mstzone_zone_id: String,
    @ColumnInfo(name = "admisecsgp_mstckp_checkpoint_id") @SerializedName("admisecsgp_mstckp_checkpoint_id") val admisecsgp_mstckp_checkpoint_id: String? = null,
    @ColumnInfo(name = "date_patroli") @SerializedName("date_patroli") val date_patroli: String? = null,

    @ColumnInfo(name = "checkin_checkpoint") @SerializedName("checkin_checkpoint") val checkin_checkpoint: String? = Utils.createdAt(),
    @ColumnInfo(name = "checkout_checkpoint") @SerializedName("checkout_checkpoint") val checkout_checkpoint: String? = null,
    @ColumnInfo(name = "status") @SerializedName("status") val status: Int = 1,

    @ColumnInfo(name = "created_at") @SerializedName("created_at") val created_at: String = Utils.createdAt(),
    @ColumnInfo(name = "synced") var synced: Boolean? = false,
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @SerializedName("detail_temuan")
    @ColumnInfo(name = "detail_temuan")
    var detailTemuan: List<ReportDetail>? = null

    override fun toString(): String {
        return "Report(sync_token=$sync_token, trans_header_id=$trans_header_id, admisecsgp_mstusr_npk=$admisecsgp_mstusr_npk, admisecsgp_mstshift_shift_id='$admisecsgp_mstshift_shift_id', admisecsgp_mstzone_zone_id='$admisecsgp_mstzone_zone_id', admisecsgp_mstckp_checkpoint_id=$admisecsgp_mstckp_checkpoint_id, date_patroli=$date_patroli, checkin_checkpoint=$checkin_checkpoint, checkout_checkpoint=$checkout_checkpoint, status=$status, created_at='$created_at', synced=$synced, detailTemuan=$detailTemuan)"
    }
}
