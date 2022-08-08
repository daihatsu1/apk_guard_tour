/*
 *     Digital Patrol Guard
 *     Object.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:01 AM
 */

package ai.digital.patrol.model

data class Object(
    val id: String? = null,
    val nama_objek: String? = null,
    val status: Boolean = false,
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