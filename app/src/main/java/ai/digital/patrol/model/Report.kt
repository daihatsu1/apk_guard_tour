/*
 *     Digital Patrol Guard
 *     Report.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 1:37 AM
 */

package ai.digital.patrol.model

data class Report(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val status: Boolean = true,
)

//{
//    "id": "307",
//    "plant_name": "PLANT 5",
//    "zone_name": "PARIMETER",
//    "zona_id": "51",
//    "bulan": "JULI",
//    "tanggal_26": "3",
//    "nama_shift": "1",
//    "status": "1"
//},