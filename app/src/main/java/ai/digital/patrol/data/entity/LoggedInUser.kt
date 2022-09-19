/*
 *     Digital Patrol Guard
 *     LoggedInUser.kt
 *     Created by ImamSyahrudin on 9/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 10:21 PM
 */

package ai.digital.patrol.data.entity

import com.google.gson.annotations.SerializedName

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    @SerializedName("key") var key: String?,
    @SerializedName("data") var user: User?,

    )

//"status": true,
//"message": "User login successful.",
//"data": {
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
//"key": "gwgo