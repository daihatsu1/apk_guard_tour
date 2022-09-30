package ai.digital.patrol.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(

    @PrimaryKey(autoGenerate = true) val userLocalId:Int,
    @ColumnInfo(name = "id") val id: String?,
    @ColumnInfo(name = "npk") val npk: String?,
    @ColumnInfo(name = "name") @SerializedName("name") val name: String?,
    @ColumnInfo(name = "admisecsgp_mstroleusr_id") @SerializedName("admisecsgp_mstroleusr_id")  val roleId: String?,
    @ColumnInfo(name = "admisecsgp_mstcmp_id") @SerializedName("admisecsgp_mstcmp_id")  val lastName: String?,
    @ColumnInfo(name = "admisecsgp_mstsite_id") @SerializedName("admisecsgp_mstsite_id")  val siteId: Boolean?,
    @ColumnInfo(name = "admisecsgp_mstplant_id") @SerializedName("admisecsgp_mstplant_id")  val palntId: String?,
    @ColumnInfo(name = "posisi") @SerializedName("posisi")  val posisi: String?,
    @ColumnInfo(name = "status") @SerializedName("status")  val status: String?,
    @ColumnInfo(name = "apikey") val apikey: String?,
)

//data": {
//    "id": "29",
//    "npk": "226198",
//    "name": "ABD. RAHMAN",
//    "password": "9e16c824d9f06109b8123f7da1296c31",
//    "admisecsgp_mstroleusr_id": "9",
//    "admisecsgp_mstcmp_id": "12",
//    "admisecsgp_mstsite_id": "9",
//    "admisecsgp_mstplant_id": "43",
//    "posisi": null,
//    "created_at": "2022-05-31 08:29:03",
//    "created_by": "1",
//    "updated_at": null,
//    "updated_by": null,
//    "status": "1"
//},