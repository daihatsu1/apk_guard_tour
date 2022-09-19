/*
 *     Digital Patrol Guard
 *     MainActivity.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 10:47 AM
 */

package ai.digital.patrol.ui.main

import ai.digital.patrol.GuardTourApplication
import ai.digital.patrol.R
import ai.digital.patrol.callback.OnInternetConnected
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.entity.Zone
import ai.digital.patrol.databinding.ActivityMainBinding
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.networking.ServiceGenerator
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.form.MainFormActivity
import ai.digital.patrol.ui.form.PatrolDataViewModel
import ai.digital.patrol.ui.form.viewadapter.ReportDetailHomeViewAdapter
import ai.digital.patrol.ui.login.LoginViewModel
import ai.digital.patrol.ui.login.LoginViewModelFactory
import ai.digital.patrol.worker.SyncViewModel
import android.app.Application
import android.app.Dialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnInternetConnected {

    private var confirmationStartPatrolCallback: DialogCallbackListener? = null
    private var confirmationStartUnscheduledPatrolCallback: DialogCallbackListener? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var btnStartUnscheduledPatrol: Button
    private lateinit var btnStartPatrol: Button
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private val scheduleAdapter = ScheduleViewAdapter()
    private var schedule: Schedule? = null
    private val reportHomeViewAdapter = ReportDetailHomeViewAdapter(null)

    private val syncViewModel by lazy {
        ViewModelProvider(this)[SyncViewModel::class.java]
    }
    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this,
            PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }

    private val scheduleViewModel by lazy {
        ViewModelProvider(
            this,
            ScheduleViewModel.Factory(Application())
        )[ScheduleViewModel::class.java]
    }
    private val loginViewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutMain.recyclerSchedule.adapter = scheduleAdapter

        val recyclerView = binding.layoutMain.recyclerSchedule
        recyclerView.adapter = scheduleAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerHomeViewReporting = binding.layoutMain.recyclerReporting
        recyclerHomeViewReporting.adapter = reportHomeViewAdapter
        recyclerHomeViewReporting.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setListReport()
        // set toolbar username
        setUsernameToolBar()
        setListener()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        btnStartUnscheduledPatrol = binding.layoutMain.startUnscheduledPatrol
        btnStartPatrol = binding.layoutMain.startPatrol

        binding.layoutToolbar.settings.setOnClickListener {
            val settingsActivity = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsActivity)
        }
        binding.layoutMain.btnUpload.setOnClickListener {
            syncViewModel.syncReportData()
        }
        binding.layoutMain.patrolReportingMore.setOnClickListener {
            val listReportActivity = Intent(this@MainActivity, ListReportActivity::class.java)
            startActivity(listReportActivity)
        }

        Utils.connectionChecker(
            this,
            applicationContext,
            binding.layoutToolbar.viewOnlineStatus,
            this
        )

        /**snip **/
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.package.ACTION_LOGOUT")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("onReceive", "Logout in progress")
                finish()
            }
        }, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        getSchedules()
        syncViewModel.syncReportData()

        val statePatrol = PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
            .getBoolean(Cons.PATROL_STATE, false)
        if (statePatrol) {
            btnStartUnscheduledPatrol.visibility = GONE
            btnStartPatrol.visibility = VISIBLE
            btnStartPatrol.text = getString(R.string.continue_patrol)
            binding.layoutMain.loadingSpinner.visibility = GONE
            binding.layoutMain.loadingSpinner.pauseAnimation()
            binding.layoutMain.tvMsgPatroldata.visibility = GONE

            btnStartPatrol.setOnClickListener {
                gotoMainForm()
            }
        } else {
            // get data patrol
            getPatrolData()
            btnStartPatrol.text = getString(R.string.start_patrol)
            // Set click action
            btnStartUnscheduledPatrol.setOnClickListener {
                dialogConfirmStartUnscheduledPatrol()
            }

            btnStartPatrol.setOnClickListener {
                schedule?.let { dialogConfirmStartPatrol(it) }
            }
        }

    }

    private fun setListReport() {

        patrolDataViewModel.getReportDetail()?.observe(this) { it ->
            if (it.isNotEmpty()) {
                binding.layoutMain.tvReportingCount.text = it.size.toString()
                binding.layoutMain.tvReportingNotFound.visibility = GONE
                binding.layoutMain.recyclerReporting.visibility = VISIBLE
                if (it.size > 3) {
                    binding.layoutMain.patrolReportingMore.visibility = VISIBLE
                }
                reportHomeViewAdapter.setList(it.take(3))
            } else {
                binding.layoutMain.tvReportingCount.text = "0"
                binding.layoutMain.patrolReportingMore.visibility = GONE
                binding.layoutMain.tvReportingNotFound.text =
                    getString(R.string.reporting_not_found)
                binding.layoutMain.tvReportingNotFound.visibility = VISIBLE
                binding.layoutMain.recyclerReporting.visibility = GONE
            }
        }
    }

    private fun getSchedules() {
        scheduleViewModel.getSchedules()?.observe(this) { it ->
            if (it != null) {
                scheduleAdapter.setSchedule(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSchedule() {
        scheduleViewModel.getSchedule()?.observe(this@MainActivity) { it ->
            if (it != null) {
                schedule = it
                if (it.shift == getString(R.string.libur)) {
                    btnStartPatrol.visibility = GONE
                    btnStartUnscheduledPatrol.visibility = VISIBLE
                } else {
                    val statePatrol =
                        PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                            .getBoolean(Cons.PATROL_STATE, false)
                    val onPatrolTime = Utils.isOnPatrolTime(it.date, it.jam_masuk, it.jam_pulang)
                    if (onPatrolTime) {
                        btnStartPatrol.visibility = VISIBLE
                    }
                    btnStartUnscheduledPatrol.visibility = VISIBLE
                }
            }
        }
    }

    private fun getPatrolData() {

        val restInterface = ServiceGenerator.createService()
        val patrolDataCall = restInterface.getPatrolData()
        patrolDataCall!!.enqueue(object : Callback<List<Zone>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<Zone>>,
                response: Response<List<Zone>>
            ) {
                if (response.isSuccessful) {
                    val zones: List<Zone>? = response.body()
                    patrolDataViewModel.insertPatrolData(zones as List<Zone>)
                    binding.layoutMain.loadingSpinner.visibility = GONE
                    binding.layoutMain.loadingSpinner.pauseAnimation()
                    if (zones.isNotEmpty()) {
                        binding.layoutMain.tvMsgPatroldata.visibility = GONE
                        getSchedule()
                    } else {
                        btnStartPatrol.visibility = GONE
                        btnStartUnscheduledPatrol.visibility = GONE

                        binding.layoutMain.btnRetryGetDataPatrol.visibility = VISIBLE
                        binding.layoutMain.tvMsgPatroldata.visibility = VISIBLE
                        binding.layoutMain.tvMsgPatroldata.text = getString(R.string.zone_not_found)
                    }
                }
            }

            override fun onFailure(call: Call<List<Zone>>, t: Throwable) {
                Log.d("ZONE", "FAIL....", t)
                binding.layoutMain.loadingSpinner.visibility = GONE
                binding.layoutMain.loadingSpinner.pauseAnimation()

                btnStartPatrol.visibility = GONE
                btnStartUnscheduledPatrol.visibility = GONE

                binding.layoutMain.btnRetryGetDataPatrol.visibility = VISIBLE
                binding.layoutMain.tvMsgPatroldata.visibility = VISIBLE
                binding.layoutMain.tvMsgPatroldata.text =
                    getString(R.string.gagal_memuat_data_zona_patroli)
            }
        })

    }
    private fun setUsernameToolBar() {
        loginViewModel.getUser()?.observe(this) {
            binding.layoutToolbar.tvToolbarUsername.text = it.name
        }
    }

    /**
     * Set listener
     *
     * initialize listener for mainActivity
     *
     */
    private fun setListener() {
        confirmationStartPatrolCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {

                dialog?.dismiss()
                val mainFormActivity = Intent(this@MainActivity, MainFormActivity::class.java)
                startActivity(mainFormActivity)
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
            }
        }

        confirmationStartUnscheduledPatrolCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
                gotoMainForm()
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {

            }
        }
    }

    fun gotoMainForm() {
        val mainFormActivity = Intent(this@MainActivity, MainFormActivity::class.java)
        startActivity(mainFormActivity)
    }

    private fun dialogConfirmStartUnscheduledPatrol() {
        val title = "APAKAH ANDA YAKIN MEMULAI PATROLI DILUAR JADWAL?"
        val subTitle = ""
        val positiveText = "LANJUTKAN"
        val negativeText = "BATAL"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationStartPatrolCallback!!
        )
            .show(supportFragmentManager, "dialogConfirmStartUnscheduledPatrol")
    }

    private fun dialogConfirmStartPatrol(schedule: Schedule) {
        val date = schedule.date?.let { Utils.dateFormat(it) }

        val title = "APAKAH ANDA YAKIN MEMULAI PATROLI SEKARANG?"
        val subTitle = "SHIFT " + schedule.shift + ", \n" + date
        val positiveText = "LANJUTKAN"
        val negativeText = "BATAL"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationStartPatrolCallback!!
        )
            .show(supportFragmentManager, "dialogConfirmStartPatrol")
    }

    override fun onConnected() {
        syncViewModel.syncReportData()
    }

}

