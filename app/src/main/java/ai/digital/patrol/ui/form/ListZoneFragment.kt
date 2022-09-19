/*
 *     Digital Patrol Guard
 *     ListZoneFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 11:19 PM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.GuardTourApplication
import ai.digital.patrol.databinding.FragmentListZoneBinding
import ai.digital.patrol.data.entity.Zone
import ai.digital.patrol.helper.Cons
import ai.digital.patrol.helper.PreferenceHelper
import ai.digital.patrol.helper.PreferenceHelper.set
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.form.listener.OnZoneClickListener
import ai.digital.patrol.ui.form.viewadapter.ZoneViewAdapter
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListZoneFragment : Fragment(), OnZoneClickListener {

    private var _binding: FragmentListZoneBinding? = null
    private val zoneViewAdapter = ZoneViewAdapter(this)
    private var confirmationPatrolDoneCallback: DialogCallbackListener? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var zones: List<Zone>
    private val patrolDataViewModel by lazy {
        ViewModelProvider(this, PatrolDataViewModel.Factory(Application())).get(
            PatrolDataViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListZoneBinding.inflate(inflater, container, false)
        setPatrolDoneDialogFragmentListener()
        _binding!!.fabPatrolDone.setOnClickListener {
            dialogConfirmPatrolDone()
        }
        val recyclerView = binding.recyclerZone
        recyclerView.adapter = zoneViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        getZones()

        return binding.root

    }

    private fun dialogConfirmPatrolDone() {
        val title = "APAKAH ANDA YAKIN TELAH SELESAI MELAKUKAN PATROLI?"
        val subTitle = ""
        val positiveText = "SELESAI PATROLI"
        val negativeText = "CEK KEMBALI"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationPatrolDoneCallback!!
        )
            .show(parentFragmentManager, "dialogConfirmPatrolDone")
    }

    private fun setPatrolDoneDialogFragmentListener() {
        confirmationPatrolDoneCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                    .set(Cons.PATROL_STATE, false)

                this@ListZoneFragment.activity?.finish()
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getZones() {
        patrolDataViewModel.getZones()?.observe(viewLifecycleOwner) { it ->
            if (it.isNotEmpty()) {
                zones = it
                zoneViewAdapter.setListZone(zones)
                val statePatrol =
                    PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())
                        .getBoolean(Cons.PATROL_STATE, false)
                if (!statePatrol) {
                    //set state patrol true
                    PreferenceHelper.appsPrefs(GuardTourApplication.applicationContext())[Cons.PATROL_STATE] =
                        true
                    val random = Random().nextInt(zones.size)
                    gotoCheckpoint(zones[random])
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(_zone: Zone) {
        gotoCheckpoint(_zone)
    }

    private fun gotoCheckpoint(_zone: Zone) {
        patrolDataViewModel.setZoneOnPatrol(_zone.id)
        val action = ListZoneFragmentDirections.actionZoneFragmentToCheckpointFragment(_zone)
        findNavController().navigate(action)
    }
}
