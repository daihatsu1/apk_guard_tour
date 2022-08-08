/*
 *     Digital Patrol Guard
 *     Zone.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.model

data class Zone(
    val plant_id: String? = null,
    val zone_id: String? = null,
    val plant: String? = null,
    val zone_name: String? = null,
    val shift: String? = null,
    val date: String? = null,
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