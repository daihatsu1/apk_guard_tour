/*
 *     Digital Patrol Guard
 *     Checkpoint.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 10:40 PM
 */

package ai.digital.patrol.model

data class Checkpoint(
    val plant_id: String? = null,
    val zone_id: String? = null,
    val plant: String? = null,
    val check_name: String? = null,
    val no_nfc: String? = null,
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