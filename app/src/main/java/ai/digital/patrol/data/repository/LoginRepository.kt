/*
 *     Digital Patrol Guard
 *     LoginRepository.kt
 *     Created by ImamSyahrudin on 13/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/13/22, 2:02 PM
 */

package ai.digital.patrol.data.repository

import ai.digital.patrol.DatabaseClient
import ai.digital.patrol.data.LoginDataSource
import ai.digital.patrol.data.dao.*
import ai.digital.patrol.data.entity.User
import ai.digital.patrol.ui.login.LoginViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource, val databaseClient: DatabaseClient) {

    // in-memory cache of the user object
    internal var user: User? = null
    private val userDao: UserDao? = DatabaseClient.getInstance()?.appDatabase?.userDao()

    private val patrolDataDao: PatrolDataDao? =
        DatabaseClient.getInstance()?.appDatabase?.patrolDataDao()
    private val scheduleDao: ScheduleDao? = DatabaseClient.getInstance()?.appDatabase?.scheduleDao()

    private val runnerScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext =
            Dispatchers.IO // no job added i.e + SupervisorJob()
    }

    fun isLoggedIn(): Boolean {
        runnerScope.launch {
            user = userDao?.getUser
        }
        return user != null
    }

    fun getUser(): LiveData<User>? {
        return userDao?.user
    }

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        runnerScope.launch {
            user = userDao?.getUser
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun logout() {
        user = null
//        DatabaseClient.getInstance()?.clearTables()
        runnerScope.launch {
            userDao?.deleteAll()
            patrolDataDao?.clearAll()
            scheduleDao?.deleteAll()

        }
    }

    fun insert(user: User) {
        runnerScope.launch {
            userDao?.setLoggedInUser(user)
        }
    }

    fun login(npk: String, password: String, loginCallback: LoginViewModel) {
        return dataSource.login(npk, password, loginCallback)
    }

    fun setUser(user: User) {
        this.user = user
        insert(user)
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null

        @JvmStatic
        fun getInstance(): LoginRepository? {
            if (instance == null) {
                instance = LoginRepository(LoginDataSource(), DatabaseClient.getInstance()!!)
            }
            return instance
        }
    }
}