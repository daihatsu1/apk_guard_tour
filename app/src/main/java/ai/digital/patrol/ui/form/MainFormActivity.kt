/*
 *     Digital Patrol Guard
 *     MainFormActivity.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 10:47 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.GuardTourApplication
import ai.digital.patrol.R
import ai.digital.patrol.data.entity.NfcData
import ai.digital.patrol.data.entity.PatrolActivity
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.databinding.ActivityMainFormBinding
import ai.digital.patrol.helper.AppEvent
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.Cons.PATROL_ACTIVITY
import ai.digital.patrol.helper.Cons.SCHEDULE
import ai.digital.patrol.helper.EventBus
import ai.digital.patrol.helper.NFCReader.toDec
import ai.digital.patrol.helper.NFCReader.toHex
import ai.digital.patrol.helper.NFCReader.toReversedDec
import ai.digital.patrol.helper.NFCReader.toReversedHex
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.PreferenceHelper.set
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.main.MainActivity
import ai.digital.patrol.ui.main.ScheduleViewModel
import android.annotation.SuppressLint
import android.app.Application
import android.app.Dialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.util.Arrays

class MainFormActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var _binding: ActivityMainFormBinding? = null
    private val binding get() = _binding!!

    val TAG = "MainFormActivity"
    var nfcAdapter: NfcAdapter? = null
    var pendingIntent: PendingIntent? = null
    var decTarget: String? = null
    private lateinit var schedule: Schedule
    private lateinit var activityPatrol: PatrolActivity
    private var disposable: Disposable? = null
    private var confirmationDoneCallback: DialogCallbackListener? = null
    private var dialogPatrolDoneShowAlready: Boolean = false

    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this, PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }
    private val scheduleViewModel by lazy {
        ViewModelProvider(
            this,
            ScheduleViewModel.Factory(Application())
        )[ScheduleViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            if (Build.VERSION.SDK_INT >= 33) {
                schedule = intent.extras?.getSerializable(SCHEDULE, Schedule::class.java)!!
                activityPatrol =
                    intent.extras?.getSerializable(PATROL_ACTIVITY, PatrolActivity::class.java)!!
            } else {
                schedule = intent.extras?.get(SCHEDULE) as Schedule
                activityPatrol = intent.extras?.get(PATROL_ACTIVITY) as PatrolActivity
            }
        }

        setSupportActionBar(binding.layoutToolbar.toolbar)

        binding.layoutToolbar.settings.visibility = GONE
        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.zone_fragment,
                R.id.checkpoint_fragment,
                R.id.object_fragment,
                R.id.reporting_fragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        setTitle()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        Utils.connectionChecker(
            this,
            applicationContext,
            binding.layoutToolbar.viewOnlineStatus,
            null
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
        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        //If no NfcAdapter, display that the device has no NFC
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null) {
            Toast.makeText(
                this, "NO NFC Capabilities",
                Toast.LENGTH_SHORT
            ).show()
        }
        val notifyIntent = Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                application,
                0,
                notifyIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                application,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    fun enableNFC(decTarget: String) {
        if (nfcAdapter != null) {
            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
            Log.d("nfcAdapter", "enable")
            Log.d("nfcAdapter", "decTarget is $decTarget")
            this.decTarget = decTarget
        }
    }

    fun disableNFC() {
        if (nfcAdapter != null) {
            nfcAdapter!!.disableForegroundDispatch(this)
            Log.d("nfcAdapter", "disable")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
        navController.enableOnBackPressed(true)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    //    setNavigationOnClickListener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_form)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun setTitle(text: String = "PATROL GUARD") {
        binding.layoutToolbar.tvToolbarUsername.text = text
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        if (title.isNullOrBlank())
            binding.layoutToolbar.tvToolbarUsername.text = "PATROL GUARD"
        else
            binding.layoutToolbar.tvToolbarUsername.text = title.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("SetTextI18n")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val data = resolveIntent(intent)
        if (data != null) {
            if (this.decTarget != null) {
                if (this.decTarget == data.dec.toString()) {
                    EventBus.post(AppEvent.NFC_MATCHED)
                    showToast("SCAN SUKSES, SILAKAN MELANJUTKAN PATROLI ANDA")
                    Log.d("nfcAdapter", "NFC_MATCHED")

                } else {
                    EventBus.post(AppEvent.NFC_NO_MATCHED)
                    showToast("CHECKPOINT TIDAK SESUAI, PERIKAS KEMBALI KARTU DAN CHEKPOINT")
                    Log.d("nfcAdapter", "NFC_NO_MATCHED")

                }
            } else {
                showToast("Terjadi kesalahan, periksa kembali kartu atau checkpoint yang di pilih")
            }
        }
    }

    private fun resolveIntent(intent: Intent): NfcData? {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = (intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?)!!
            return detectTagData(tag)
        }
        return null
    }

    //For detection
    private fun detectTagData(tag: Tag?): NfcData? {
        val sb = StringBuilder()
        val id = tag!!.id
        sb.append("ID (hex): ").append(toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
        val data = toHex(id)?.let {
            NfcData(
                Arrays.toString(tag.id),
                it,
                toReversedHex(id),
                toDec(id),
                toReversedDec(id),
                tag.techList
            )
        }
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        Log.v(TAG, data.toString())
        return data
    }

    private fun getSchedule() {
        scheduleViewModel.getSchedule()?.observe(this) { it ->
            if (it != null) {
                schedule = it
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getSchedule()
        disposable =
            EventBus.subscribe<AppEvent>()
                // if you want to receive the event on main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ appEvent ->
                    Log.d("EVENTBUS", "event received: $appEvent")
                    if (appEvent == AppEvent.PATROL_TIME_OFF) {
                        val statePatrol =
                            PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                                .getBoolean(Cons.PATROL_STATE, false)
                        if (statePatrol) {
                            val stateUnschedulePatrol =
                                PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                                    .getBoolean(Cons.UNSCHEDULE_PATROL_STATE, false)
                            if (!stateUnschedulePatrol) {
                                // force to finish patroli
                                if (!dialogPatrolDoneShowAlready) {
                                    dialogConfirmDone()
                                }
                            }
                        }
                        disposable?.dispose()
                    }

                }, {
                    Log.e("EVENTBUS", it.message.toString())
                })

    }

    private fun dialogConfirmDone() {
        dialogPatrolDoneShowAlready = true
        setPatrolDoneDialogFragmentListener()
        val title = "WAKTU PATROLI TELAH HABIS, KLIK SELESAI UNTUK KEMBALI KE HALAMAN UTAMA?"
        val subTitle = ""
        val positiveText = "SELESAI PATROLI"
        val negativeText = "CEK KEMBALI"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            null,
            confirmationDoneCallback!!
        ).show(supportFragmentManager, "dialogConfirmZoneDone")
    }

    private fun setPatrolDoneDialogFragmentListener() {
        confirmationDoneCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {

                schedule.id_jadwal_patroli?.let { patrolDataViewModel.setPatrolActivityDone(it) }
                PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())[Cons.PATROL_STATE] =
                    false
                PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())[Cons.UNSCHEDULE_PATROL_STATE] =
                    false

                val gotoMain = Intent(this@MainFormActivity, MainActivity::class.java)
                startActivity(gotoMain)
                finishAndRemoveTask()
                dialog?.dismiss()
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
            }

        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()

    }
}