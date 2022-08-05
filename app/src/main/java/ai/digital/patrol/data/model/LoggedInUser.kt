/*
 *     Digital Patrol Guard
 *     LoggedInUser.kt
 *     Created by ImamSyahrudin on 5/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/5/22, 10:32 AM
 */

package ai.digital.patrol.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String
)