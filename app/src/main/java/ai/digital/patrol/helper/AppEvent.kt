/*
 *     Digital Patrol Guard
 *     AppEvent.kt
 *     Created by ImamSyahrudin on 18/9/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 9/18/22, 11:23 AM
 */

package ai.digital.patrol.helper

enum class AppEvent {
    NFC_MATCHED,
    NFC_NO_MATCHED,
    PATROL_TIME_ON,
    PATROL_TIME_OFF,
    PATROL_SHIFT_ON,
    PATROL_SHIFT_OFF,
    PATROL_ON_SHIFT_ON_TIME,
    PATROL_OFF_SHIFT_ON_TIME,
    PATROL_ON_SHIFT_OFF_TIME,
    PATROL_OFF_SHIFT_OFF_TIME,
}
//data class AppEvent(val text: String)
