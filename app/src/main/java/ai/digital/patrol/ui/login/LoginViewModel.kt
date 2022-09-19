/*
 *     Digital Patrol Guard
 *     LoginViewModel.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.login

import ai.digital.patrol.GuardTourApplication
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import ai.digital.patrol.data.repository.LoginRepository

import ai.digital.patrol.R
import ai.digital.patrol.callback.LoginCallback
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.PreferenceHelper.set
import ai.digital.patrol.data.entity.LoggedInUser
import ai.digital.patrol.data.entity.User
import android.util.Log
import retrofit2.Call
import retrofit2.Response

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel(), LoginCallback {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password, this)
    }
    fun logout() {
        // can be launched in a separate asynchronous job
        loginRepository.logout()
    }

    fun isLoggedIn(): Boolean {
        Log.d("IS_LOGGED_IN","isLoggedIn "+ loginRepository.isLoggedIn())
        return loginRepository.isLoggedIn()
    }
    fun getUser(): LiveData<User>? {
        return loginRepository.getUser()
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    override fun onResponse(call: Call<LoggedInUser?>, response: Response<LoggedInUser?>) {
        if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                data.user?.let { loginRepository.setUser(it) }
                PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext()).set("apiKey", data.key)
                PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext()).set("userId",
                    data.user?.npk
                )
                _loginResult.value =
                    LoginResult(
                        success = LoggedInUserView(
                            displayName = data.user?.name.toString(),
                            npk = data.user?.npk.toString()
                        )
                    )
            }else{
                _loginResult.value = LoginResult(error = R.string.login_failed_server)
            }
        }else{
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    override fun onFailure(call: Call<LoggedInUser?>, t: Throwable) {
        _loginResult.value = LoginResult(error = R.string.login_failed)
    }
}