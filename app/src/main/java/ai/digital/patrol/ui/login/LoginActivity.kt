/*
 *     Digital Patrol Guard
 *     LoginActivity.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.login

import ai.digital.patrol.R
import ai.digital.patrol.databinding.ActivityLoginBinding
import ai.digital.patrol.helper.NFCReader
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.ui.main.MainActivity
import android.app.Activity
//import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    var loadingProgressBar: ProgressBar? = null
    val TAG: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setConfig(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        val usernameEditText = binding.layoutLogin.username
        val passwordEditText = binding.layoutLogin.password
        val loginButton = binding.layoutLogin.login
        loadingProgressBar = binding.layoutLogin.loading

        loginViewModel.loginFormState.observe(this, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            loginButton.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                usernameEditText.error = getString(loginFormState.usernameError)
            }
        })

        loginViewModel.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null) {
                return@Observer
            }
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

        })
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()

                )
            }
            false
        }
        loginButton.setOnClickListener {
            loadingProgressBar!!.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()

            )
        }

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        setResult(Activity.RESULT_CANCELED)
        finishAffinity()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
        loadingProgressBar!!.visibility = View.GONE
    }

}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}