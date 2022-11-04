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
import ai.digital.patrol.data.entity.PatrolActivity
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.entity.Shift
import ai.digital.patrol.data.entity.Zone
import ai.digital.patrol.databinding.ActivityMainBinding
import ai.digital.patrol.helper.AppEvent
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.Cons.PATROL_ACTIVITY
import ai.digital.patrol.helper.Cons.SCHEDULE
import ai.digital.patrol.helper.EventBus
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.PreferenceHelper.set
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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
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
    private var currentShift: Shift? = null
    private var zones: List<Zone>? = null
    private val reportHomeViewAdapter = ReportDetailHomeViewAdapter(null)
    private val listZoneString: ArrayList<String> = ArrayList()
    private val reportListLimit = 3
    private var disposable: Disposable? = null
    private var isInternetConnected: Boolean = false
    private var patrolActivity: PatrolActivity? = null

    private val runnableCode = object : Runnable {
        override fun run() {
            if (schedule != null) {
                if (schedule!!.jam_pulang != null && schedule!!.jam_masuk != null) {
                    val isOnSchedule = Utils.isOnPatrolTime(
                        schedule!!.date, schedule!!.jam_masuk, schedule!!.jam_pulang
                    )
                    Log.d("Current Shift", currentShift.toString())
                    if (currentShift != null) {
                        val isCurrentShiftPatrolTime = Utils.isOnShiftPatrolTime(
                            schedule!!.date, currentShift!!.jam_masuk, currentShift!!.jam_pulang
                        )

                        if (isCurrentShiftPatrolTime) {
                            EventBus.post(AppEvent.PATROL_SHIFT_ON)
                        } else {
                            EventBus.post(AppEvent.PATROL_SHIFT_OFF)
                        }

                        if (isOnSchedule) {
                            EventBus.post(AppEvent.PATROL_TIME_ON)

                        } else {
                            EventBus.post(AppEvent.PATROL_TIME_OFF)
                        }
                    } else {
                        EventBus.post(AppEvent.PATROL_SHIFT_OFF)
                    }

                    binding.layoutMain.btnRetryGetDataPatrol.visibility = GONE
                    binding.layoutMain.tvMsgPatroldata.visibility = GONE
                    binding.layoutMain.loadingSpinner.visibility = GONE
                    binding.layoutMain.loadingSpinner.pauseAnimation()
                }
            }
            Handler(Looper.getMainLooper()).postDelayed(this, 5000)
        }
    }

    private val syncViewModel by lazy {
        ViewModelProvider(this)[SyncViewModel::class.java]
    }
    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this, PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }

    private val scheduleViewModel by lazy {
        ViewModelProvider(
            this, ScheduleViewModel.Factory(Application())
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
        getSchedules()
        runnableCode.run()
        setListReport()
        // set toolbar username
        setUsernameToolBar()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        btnStartUnscheduledPatrol = binding.layoutMain.startUnscheduledPatrol
        btnStartPatrol = binding.layoutMain.startPatrol

        btnStartUnscheduledPatrol.visibility = GONE
        btnStartPatrol.visibility = GONE

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
        binding.layoutMain.btnRetryGetDataPatrol.setOnClickListener {
            binding.layoutMain.loadingSpinner.visibility = VISIBLE
            binding.layoutMain.loadingSpinner.playAnimation()
            getZones()
        }

        Utils.connectionChecker(
            this, applicationContext, binding.layoutToolbar.viewOnlineStatus, this
        )
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.package.ACTION_LOGOUT")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("onReceive", "Logout in progress")
                finish()
            }
        }, intentFilter)
        getAllCheckpoint()
    }

    override fun onResume() {
        super.onResume()
        getSchedules()
        syncViewModel.syncReportData()
        val statePatrol = PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
            .getBoolean(Cons.PATROL_STATE, false)
        val stateUnschedulePatrol =
            PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                .getBoolean(Cons.UNSCHEDULE_PATROL_STATE, false)
        schedule?.id_jadwal_patroli?.let { getPatrolActivity(it) }

        binding.layoutMain.loadingSpinner.visibility = VISIBLE
        binding.layoutMain.loadingSpinner.playAnimation()

        btnStartPatrol.visibility = GONE
        btnStartUnscheduledPatrol.visibility = GONE

        if (statePatrol) {
            currentShift = scheduleViewModel.getPatrolShift()
            btnStartUnscheduledPatrol.visibility = GONE
            btnStartPatrol.visibility = VISIBLE
            btnStartPatrol.text = getString(R.string.continue_patrol)

            binding.layoutMain.loadingSpinner.visibility = GONE
            binding.layoutMain.loadingSpinner.pauseAnimation()
            binding.layoutMain.tvMsgPatroldata.visibility = GONE

            btnStartPatrol.setOnClickListener {
                gotoMainForm()
            }
        } else if (stateUnschedulePatrol) {
            currentShift = scheduleViewModel.getPatrolShift()
            btnStartUnscheduledPatrol.visibility = VISIBLE
            btnStartUnscheduledPatrol.text = getString(R.string.continue_patrol)

            btnStartPatrol.visibility = GONE

            binding.layoutMain.loadingSpinner.visibility = GONE
            binding.layoutMain.loadingSpinner.pauseAnimation()
            binding.layoutMain.tvMsgPatroldata.visibility = GONE

            btnStartUnscheduledPatrol.setOnClickListener {
                gotoMainForm()
            }
        } else {
            currentShift = scheduleViewModel.getCurrentShift()

            // get data patrol
            when (patrolActivity?.status) {
                1 -> {
                    binding.layoutMain.startPatrol.visibility = GONE
                    getPatrolDataAPI()
                }

                else -> getPatrolDataAPI()
            }
            btnStartPatrol.text = getString(R.string.start_patrol)
            btnStartUnscheduledPatrol.text = getString(R.string.patroli_diluar_jadwal)

            // Set click action
            btnStartUnscheduledPatrol.setOnClickListener {
                dialogConfirmStartUnscheduledPatrol()
            }

            btnStartPatrol.setOnClickListener {
                schedule?.let { it1 -> dialogConfirmStartPatrol(it1) }
            }
        }

        disposable = EventBus.subscribe<AppEvent>().observeOn(AndroidSchedulers.mainThread())
            .subscribe({ appEvent ->
                Log.d("EVENTBUS", "event received: $appEvent")
                val statePatrolBus =
                    PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                        .getBoolean(Cons.PATROL_STATE, false)
                getZones()

                binding.layoutMain.btnRetryGetDataPatrol.visibility = GONE
                binding.layoutMain.loadingSpinner.visibility = GONE
                binding.layoutMain.tvMsgPatroldata.visibility = GONE
                if (zones?.isNotEmpty() == true && schedule != null) {
                    if (appEvent == AppEvent.PATROL_SHIFT_OFF) {
                        if (statePatrolBus) {
                            PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())[Cons.PATROL_STATE] =
                                false
                            if (binding.layoutMain.startPatrol.isVisible) {
                                binding.layoutMain.startPatrol.visibility = GONE
                            }
                            if (binding.layoutMain.startUnscheduledPatrol.isVisible) {
                                binding.layoutMain.startUnscheduledPatrol.visibility = GONE
                            }
                        } else {
                            binding.layoutMain.loadingSpinner.visibility = GONE
                            binding.layoutMain.loadingSpinner.pauseAnimation()
                            btnStartPatrol.visibility = GONE
                            btnStartUnscheduledPatrol.visibility = GONE
                            binding.layoutMain.btnRetryGetDataPatrol.visibility = GONE
                            binding.layoutMain.tvMsgPatroldata.visibility = VISIBLE
                            binding.layoutMain.tvMsgPatroldata.text =
                                getString(R.string.transition_patrol_time_shfit)
                        }
                    }

                    if (appEvent == AppEvent.PATROL_TIME_OFF) {
                        if (statePatrolBus) {
                            PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())[Cons.PATROL_STATE] =
                                false
                            if (binding.layoutMain.startPatrol.isVisible) {
                                binding.layoutMain.startPatrol.visibility = GONE
                            }

                        } else {
                            if (binding.layoutMain.startPatrol.isVisible) {
                                binding.layoutMain.startPatrol.visibility = GONE
                            }
                            if (binding.layoutMain.startUnscheduledPatrol.isGone) binding.layoutMain.startUnscheduledPatrol.visibility =
                                VISIBLE
                        }
                    } else {
                        schedule?.id_jadwal_patroli?.let { getPatrolActivity(it) }
                        if (isInternetConnected and statePatrolBus) {
                            when (patrolActivity?.status) {
                                0 -> binding.layoutMain.startPatrol.visibility =
                                    VISIBLE

                                1 -> binding.layoutMain.startPatrol.visibility =
                                    GONE

                                else -> binding.layoutMain.startPatrol.visibility =
                                    VISIBLE
                            }

                        } else if (isInternetConnected and !statePatrolBus) {
                            binding.layoutMain.startPatrol.visibility = VISIBLE
                            binding.layoutMain.startUnscheduledPatrol.visibility =
                                VISIBLE
                            when (patrolActivity?.status) {
                                0 -> binding.layoutMain.startPatrol.visibility =
                                    VISIBLE

                                1 -> binding.layoutMain.startPatrol.visibility = GONE

                                else -> binding.layoutMain.startPatrol.visibility = VISIBLE
                            }

                        } else if (!isInternetConnected and statePatrolBus) {
                            binding.layoutMain.startUnscheduledPatrol.visibility =
                                GONE
                            when (patrolActivity?.status) {
                                0 -> binding.layoutMain.startPatrol.visibility =
                                    VISIBLE

                                1 -> binding.layoutMain.startPatrol.visibility = GONE
                            }

                        } else if (!isInternetConnected and !statePatrolBus) {
                            binding.layoutMain.startPatrol.visibility = GONE
                            binding.layoutMain.startUnscheduledPatrol.visibility = GONE

                            binding.layoutMain.btnRetryGetDataPatrol.visibility = VISIBLE
                            binding.layoutMain.tvMsgPatroldata.visibility = VISIBLE
                        }
                    }
                } else {
                    binding.layoutMain.loadingSpinner.visibility = GONE
                    binding.layoutMain.loadingSpinner.pauseAnimation()

                    btnStartPatrol.visibility = GONE
                    btnStartUnscheduledPatrol.visibility = GONE

                    binding.layoutMain.btnRetryGetDataPatrol.visibility = VISIBLE
                    binding.layoutMain.tvMsgPatroldata.visibility = VISIBLE
                    binding.layoutMain.tvMsgPatroldata.text =
                        getString(R.string.gagal_memuat_data_zona_patroli)
                }
            }, {
                Log.e("EVENTBUS", it.message.toString())
            })

    }

    override fun onPause() {
        disposable?.dispose()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        schedule = null
    }

    private fun getPatrolActivity(idJadwal: String) {
        patrolDataViewModel.getPatrolActivity(idJadwal)?.observe(this) {
            if (it == null) {
                patrolDataViewModel.getPatrolActivityApi(idJadwal)
            }
            patrolActivity = it
        }
    }

    private fun setListReport() {
        val recyclerHomeViewReporting = binding.layoutMain.recyclerReporting
        recyclerHomeViewReporting.adapter = reportHomeViewAdapter
        recyclerHomeViewReporting.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        patrolDataViewModel.getReportDetail()?.observe(this) {
            if (it.isNotEmpty()) {
                binding.layoutMain.tvReportingCount.text = it.size.toString()
                binding.layoutMain.layoutNotFound.visibility = GONE
                binding.layoutMain.recyclerReporting.visibility = VISIBLE

                if (it.size > reportListLimit) {
                    binding.layoutMain.patrolReportingMore.visibility = VISIBLE
                }
                reportHomeViewAdapter.setList(it.take(reportListLimit))
            } else {

                binding.layoutMain.tvReportingCount.text = "0"
                binding.layoutMain.patrolReportingMore.visibility = GONE
                binding.layoutMain.layoutNotFound.visibility = VISIBLE
                binding.layoutMain.tvReportingNotFound.text =
                    getString(R.string.reporting_not_found)
                binding.layoutMain.tvReportingNotFound.visibility = VISIBLE
                binding.layoutMain.recyclerReporting.visibility = GONE
            }
        }
    }

    private fun getSchedules() {
        scheduleViewModel.getSchedules()?.observe(this) {
            if (it != null) {
                scheduleAdapter.setSchedule(it)
                setListener()
                getSchedule()
            }
        }
    }

    private fun getSchedule() {
        scheduleViewModel.getSchedule()?.observe(this@MainActivity) {
            if (it != null) {
                schedule = it
                it.id_jadwal_patroli?.let { it1 -> getPatrolActivity(it1) }
            }
        }
    }

    private fun getZones() {
        patrolDataViewModel.getZones()?.observe(this@MainActivity) {
            if (it.isNotEmpty()) {
                zones = it
                setZoneListDialog(zones!!)
            } else {
                getPatrolDataAPI()
            }
        }
    }

    private fun getShift() {
        scheduleViewModel.getShift()?.observe(this) {
            if (it.isNotEmpty()) {
                currentShift = scheduleViewModel.getCurrentShift()
            }
        }
    }

    private fun getPatrolDataAPI() {
        getShift()
        val restInterface = ServiceGenerator.createService()
        val patrolDataCall = restInterface.getPatrolData()
        patrolDataCall!!.enqueue(object : Callback<List<Zone>> {
            override fun onResponse(
                call: Call<List<Zone>>, response: Response<List<Zone>>
            ) {
                if (response.isSuccessful) {
                    zones = response.body()
                    patrolDataViewModel.insertPatrolData(zones as List<Zone>)
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

    private fun setZoneListDialog(zones: List<Zone>) {
        listZoneString.clear()
        zones.forEach {
            if (it.zone_name != null) listZoneString.add(it.zone_name)
        }
    }


    private fun getAllCheckpoint() {
        patrolDataViewModel.getAllCheckpoint()?.observe(this) {
            if (it.isNotEmpty()) {
                val checkpointDone = it.filter { ch ->
                    ch.patrol_status == false
                }
                val total = it.size
                val totalCheckpointDone = checkpointDone.size
                val percentage = (totalCheckpointDone.toDouble() / total.toDouble()) * 100
                val textTarget = "$totalCheckpointDone/$total"

                binding.layoutMain.tvReportingTarget.text = textTarget
                binding.layoutMain.tvReportingPersentage.text = buildString {
                    append(String.format("%.1f", percentage))
                    append("%")
                }
            } else {
                Log.d("TEMUAN", "NOT FOUND TEMUAN")
            }
        }
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

                schedule?.id_jadwal_patroli?.let {
                    patrolDataViewModel.setPatrolActivityStart(it)
                }
                val statePatrol =
                    PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                        .getBoolean(Cons.PATROL_STATE, false)
                if (!statePatrol) {
                    PreferenceHelper.appsPrefs(
                        GuardTourApplication
                            .applicationContext()
                    )[Cons.PATROL_STATE] =
                        true
                    PreferenceHelper.appsPrefs(
                        GuardTourApplication
                            .applicationContext()
                    )[Cons.RANDOM_ZONE] =
                        true
                }
                dialog?.dismiss()
                val mainFormActivity = Intent(this@MainActivity, MainFormActivity::class.java)
                intent.putExtra(SCHEDULE, schedule)
                intent.putExtra(PATROL_ACTIVITY, patrolActivity)

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
                schedule?.id_jadwal_patroli?.let {
                    patrolDataViewModel.setPatrolActivityStart(it)
                }

                currentShift?.shift_id?.let { patrolDataViewModel.setPatrolRunningShift("1") }

                val unScheduleStatePatrol =
                    PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                        .getBoolean(Cons.UNSCHEDULE_PATROL_STATE, false)
                if (!unScheduleStatePatrol) {
                    PreferenceHelper.appsPrefs(
                        GuardTourApplication
                            .applicationContext()
                    )[Cons.UNSCHEDULE_PATROL_STATE] =
                        true
                    PreferenceHelper.appsPrefs(
                        GuardTourApplication
                            .applicationContext()
                    )[Cons.RANDOM_ZONE] =
                        true
                    patrolDataViewModel.resetDataReport()
                }
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
        val title = getString(R.string.start_patrol_unschedule)
        val subTitle = ""
        val positiveText = getString(R.string.lanjutkan)
        val negativeText = getString(R.string.batal)

        DialogFragment.newInstance(
            title = title,
            subTitle = subTitle,
            positiveText = positiveText,
            negativeText = negativeText,
            dialogCallbackListener = confirmationStartUnscheduledPatrolCallback!!,
            showList = true,
            dataList = listZoneString
        ).show(supportFragmentManager, "dialogConfirmStartUnscheduledPatrol")
    }

    private fun dialogConfirmStartPatrol(schedule: Schedule) {
        val date = schedule.date?.let { Utils.scheduleDateFormat(it) }

        val title = getString(R.string.start_patrol_title)
        val subTitle = "SHIFT " + schedule.shift + ", " + date
        val positiveText = getString(R.string.lanjutkan)
        val negativeText = getString(R.string.batal)

        DialogFragment.newInstance(
            title = title,
            subTitle = subTitle,
            positiveText = positiveText,
            negativeText = negativeText,
            dialogCallbackListener = confirmationStartPatrolCallback!!,
            showList = true,
            dataList = listZoneString
        ).show(supportFragmentManager, "dialogConfirmStartPatrol")
    }

    override fun onConnected() {
        isInternetConnected = true
        syncViewModel.syncReportData()
    }

}

