/*
 *     Digital Patrol Guard
 *     ListZoneFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/7/22, 11:19 PM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.R
import ai.digital.patrol.databinding.FragmentListZoneBinding
import ai.digital.patrol.model.Zone
import ai.digital.patrol.ui.form.listener.OnZoneClickListener
import ai.digital.patrol.ui.form.viewadapter.ZoneViewAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListZoneFragment : Fragment(), OnZoneClickListener {

    private var _binding: FragmentListZoneBinding? = null
    private val zoneViewAdapter = ZoneViewAdapter(this)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListZoneBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerZone
        recyclerView.adapter = zoneViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        setListZone()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setListZone(){
        val zone: List<Zone> = listOf<Zone>(
            Zone("1","1","PLANT 5","ZONE 1","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 2","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 3","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 4","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 5","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 6","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 7","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
            Zone("1","1","PLANT 5","ZONE 8","SHIFT 1", "SENIN, 8 AGUSTUS 2022"),
        )
        Log.d("ZONE_RAW", zone.toString())
        zoneViewAdapter.setListZone(zone=zone)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(_zone: Zone) {
        findNavController().navigate(R.id.action_ZoneFragment_to_CheckpointFragment)
    }
}