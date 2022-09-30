/*
 *     Digital Patrol Guard
 *     LoginViewModelFactory.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.login

import ai.digital.patrol.DatabaseClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ai.digital.patrol.data.LoginDataSource
import ai.digital.patrol.data.repository.LoginRepository
import android.app.Application

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(),
                    databaseClient = DatabaseClient.getInstance()!!
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}