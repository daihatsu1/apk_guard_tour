/*
 *     Digital Patrol Guard
 *     LoginDataSource.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.data

import ai.digital.patrol.callback.LoginCallback
import ai.digital.patrol.networking.ServiceGenerator

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(npk: String, password: String, callback: LoginCallback?)  {
//        try {
//            // TODO: handle loggedInUser authentication
////            val user = User("", "Jane Doe")
//            val restInterface = ServiceGenerator.createService()
//            val loginCall = restInterface.login(npk, password)
//            return Result.Success()
//        } catch (e: Throwable) {
//            return Result.Error(IOException("Error logging in", e))
//        }

        val restInterface = ServiceGenerator.createService()
        val loginCall = restInterface.login(npk, password)
        callback?.let { loginCall!!.enqueue(it) }

    }

}