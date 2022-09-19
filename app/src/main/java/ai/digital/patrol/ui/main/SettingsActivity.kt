/*
 *     Digital Patrol Guard
 *     SettingsActivity.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 12:58 AM
 */

package ai.digital.patrol.ui.main

import ai.digital.patrol.AppDatabase
import ai.digital.patrol.DatabaseClient
import ai.digital.patrol.R
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.PreferenceHelper.clearAll
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.login.LoginViewModel
import ai.digital.patrol.ui.login.LoginViewModelFactory
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pengaturan"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val loginViewModel by lazy {
            ViewModelProvider(this, LoginViewModelFactory()).get(
                LoginViewModel::class.java
            )
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val logout: Preference? = findPreference("logout")
            logout?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                dialogLogout()
                true
            }
            val viewMode: SwitchPreferenceCompat? = findPreference(Cons.VIEW_MODE)
            viewMode?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    if (newValue as Boolean) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        true
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        true
                    }
                }
        }


        private fun dialogLogout() {
            val title = "APAKAH ANDA YAKIN UNTUK LOGOUT?"
            val subTitle = "PASTIKAN SEMUA DATA PATROLI TELAH TERUPLOAD SEBELUM ANDA LOGOUT!"
            val positiveText = "LOGOUT"
            val negativeText = "BATAL"

            DialogFragment.newInstance(
                title,
                subTitle,
                positiveText,
                negativeText,
                object : DialogCallbackListener {
                    override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                        dialog?.dismiss()
                        actionLogout()
                    }

                    override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                        dialog?.dismiss()
                    }

                    override fun onDismissClickListener(dialog: DialogInterface) {
                    }
                }
            )
                .show(parentFragmentManager, "dialogConfirmStartUnscheduledPatrol")
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun actionLogout() {
            activity?.applicationContext?.let { PreferenceHelper.appsPrefs(it).clearAll() }
            Handler(Looper.getMainLooper()).postDelayed({
                loginViewModel.logout()
                DatabaseClient.getInstance()?.clearTables()
                val broadcastIntent = Intent()
                broadcastIntent.action = "com.package.ACTION_LOGOUT"
                activity?.sendBroadcast(broadcastIntent)
                activity?.finishAffinity()
                Toast.makeText(context, "Logout", Toast.LENGTH_LONG).show()

                android.os.Process.killProcess(android.os.Process.myPid());
                exitProcess(0);

            }, 1500)
        }
    }


}