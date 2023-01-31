/*
 *     Digital Patrol Guard
 *     ListCheckpointFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 10:42 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.GuardTourApplication
import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.Report
import ai.digital.patrol.data.entity.Schedule
import ai.digital.patrol.data.entity.Zone
import ai.digital.patrol.databinding.FragmentListCheckpointBinding
import ai.digital.patrol.helper.*
import ai.digital.patrol.helper.PreferenceHelper.set
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.dialog.DialogNFCFragment
import ai.digital.patrol.ui.form.listener.OnCheckpointClickListener
import ai.digital.patrol.ui.form.viewadapter.CheckpointViewAdapter
import ai.digital.patrol.ui.main.ScheduleViewModel
import ai.digital.patrol.worker.SyncViewModel
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ListCheckpointFragment : Fragment(), OnCheckpointClickListener {

    private var _binding: FragmentListCheckpointBinding? = null
    private val checkpointViewAdapter = CheckpointViewAdapter(this)
    private val args: ListCheckpointFragmentArgs by navArgs()
    lateinit var zone: Zone
    private lateinit var schedule: Schedule
    private var confirmationZoneDoneCallback: DialogCallbackListener? = null
    private var clickBackPressed: Boolean = true

    lateinit var checkpoint: Checkpoint
    private var onBackPressedCallback: OnBackPressedCallback? = null
    private var disposable: Disposable? = null
    private var dialogNfcFragment: DialogNFCFragment? = null

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
    private val syncViewModel by lazy {
        ViewModelProvider(this)[SyncViewModel::class.java]
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        args.let {
            zone = it.dataZone!!
        }
        _binding = FragmentListCheckpointBinding.inflate(inflater, container, false)
        _binding!!.titleZone.text = buildString {
            append("PILIH CHECKPOINT ZONA ")
            append(zone.zone_name)
        }
        val recyclerView = binding.recyclerCheckpoint
        recyclerView.adapter = checkpointViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        getSchedule()
        setListCheckpoint()
        setPatrolDoneDialogFragmentListener()
        _binding!!.fabZoneDone.setOnClickListener {
            dialogConfirmZoneDone(zone)
        }
        _binding!!.fabZoneBack.setOnClickListener {
            clickBackPressed = false
            activity?.onBackPressed()
        }
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (clickBackPressed) {
//                    dialogConfirmZoneDone(zone)
                } else {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, onBackPressedCallback as OnBackPressedCallback
        )
        disposable =
            EventBus.subscribe<AppEvent>()
                // if you want to receive the event on main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ appEvent ->
                    Log.d("EVENTBUS", "event received: $appEvent")
                    if (appEvent == AppEvent.NFC_MATCHED) {
                        if (dialogNfcFragment?.showsDialog == true) {
                            dialogNfcFragment!!.dismiss()
                            addReport(checkpoint)
                        }
                    }

                }, {
                    Log.e("EVENTBUS", it.message.toString())
                })

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun dialogNfc(_checkpoint: Checkpoint) {
        dialogNfcFragment = DialogNFCFragment.newInstance(object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
                addReport(_checkpoint)
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
                dialog.dismiss()
            }

        }, _checkpoint)
        dialogNfcFragment!!.show(parentFragmentManager, "dialogConfirmStartPatrol")

    }

    private fun setListCheckpoint() {
        patrolDataViewModel.getCheckpointByZone(zone.id)?.observe(viewLifecycleOwner) { it ->
            checkpointViewAdapter.setList(it)

            val totalCheckpoint = it.size
            val totalCheckpointChecked = it.filter {
                it.patrol_status != null
            }.size

            if (totalCheckpointChecked < totalCheckpoint) {
                binding.fabZoneDone.visibility = View.GONE
            } else {
                binding.fabZoneDone.visibility = VISIBLE
            }
        }
    }

    private fun getSchedule() {
        scheduleViewModel.getSchedule()?.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                schedule = it
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun addReport(_checkpoint: Checkpoint) {
        Log.d("CHECKPOINT REPORT", checkpoint.toString())
        val npk = PreferenceHelper.appsPrefs(this.requireContext()).getString(Cons.NPK, "")
        var reportData: Report? = null

        GlobalScope.launch(Dispatchers.IO) {
            reportData = patrolDataViewModel.getReportByCheckpointId(_checkpoint.id)
            if (reportData == null) {
                val syncToken = UUID.randomUUID().toString() +"-"+ System.currentTimeMillis().toString()
                val newReport = Report(
                    sync_token = syncToken,
                    admisecsgp_mstckp_checkpoint_id = _checkpoint.id,
                    admisecsgp_mstshift_shift_id = schedule.shift_id!!,
                    admisecsgp_mstusr_npk = npk,
                    admisecsgp_mstzone_zone_id = zone.id,
                    date_patroli = Utils.createdAt("yyyy-MM-dd"),
                    checkin_checkpoint = Utils.createdAt("yyyy-MM-dd HH:mm:ss"),
                    created_at = Utils.createdAt("yyyy-MM-dd HH:mm:ss"),
                )
                val statePatrol =
                    PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                        .getBoolean(Cons.PATROL_STATE, false)
                if (statePatrol) {
                    newReport.type_patrol = 0
                } else {
                    newReport.type_patrol = 1
                }
                reportData = patrolDataViewModel.insertReport(newReport)
            }
            gotoObject(reportData, _checkpoint) // ...back on Main
        }

        PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())[Cons.ALLOW_BACK_BUTTON] =
            true
    }

    private fun gotoObject(reportData: Report?, _checkpoint: Checkpoint) {
        if (reportData != null) {
            activity?.runOnUiThread {
                patrolDataViewModel.checkInCheckpoint(_checkpoint.id)
                val action =
                    ListCheckpointFragmentDirections.actionCheckpointFragmentToListObjectFragment(
                        _checkpoint, reportData
                    )
                findNavController().safeNavigate(action)
                syncViewModel.syncReportData()
            }
        }

    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(_checkpoint: Checkpoint) {
        checkpoint = _checkpoint
        if (_checkpoint.patrol_status == true) {
            addReport(_checkpoint)
        } else {
            dialogNfc(_checkpoint)
        }
    }

    private fun dialogConfirmZoneDone(zone: Zone) {
        val title = "APAKAH ANDA YAKIN TELAH SELESAI MELAKUKAN PATROLI DI ZONA {${zone.zone_name}}?"
        val subTitle = ""
        val positiveText = "SELESAI ZONA"
        val negativeText = "CEK KEMBALI"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationZoneDoneCallback!!
        ).show(parentFragmentManager, "dialogConfirmZoneDone")
    }

    private fun setPatrolDoneDialogFragmentListener() {
        confirmationZoneDoneCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                patrolDataViewModel.setZoneOnPatrolDone(zone.id)
                clickBackPressed = false
                activity?.onBackPressed()
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

    override fun onResume() {
        super.onResume()
        activity?.title = "DAFTAR CHECKPOINT PATROLI"
        syncViewModel.syncReportData()
        val backButtonShow = PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
            .getBoolean(Cons.ALLOW_BACK_BUTTON, false)
        if (backButtonShow) {
            binding.fabZoneBack.visibility = VISIBLE
        }
        disposable =
            EventBus.subscribe<AppEvent>()
                // if you want to receive the event on main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("EVENTBUS", "event received: $it")
                }

    }
}
