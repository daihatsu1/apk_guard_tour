/*
 *     Digital Patrol Guard
 *     ListObjectFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:06 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.R
import ai.digital.patrol.databinding.FragmentListObjectBinding
import ai.digital.patrol.model.Object
import ai.digital.patrol.ui.form.listener.OnObjectClickListener
import ai.digital.patrol.ui.form.viewadapter.ObjectViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

class ListObjectFragment : Fragment(), OnObjectClickListener {

    companion object {
        fun newInstance() = ListObjectFragment()
    }

    private var _binding: FragmentListObjectBinding? = null
    private val objectViewAdapter = ObjectViewAdapter(this)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListObjectBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerObject
        recyclerView.adapter = objectViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        setListObject()

        binding.titleReportListsBtn.setOnClickListener{
            findNavController().navigate(R.id.action_object_fragment_to_listReportFragment)
        }

        return binding.root
    }

    private fun setListObject() {
        val objectList: List<Object> = listOf<Object>(
            Object("1", "LAMPU AREA DALAM", true),
            Object("2", "LAMPU DISKO", false),
            Object("3", "HYDRANT AREA KEC ( HYDRANT NO 23 )", false),
            Object("4", "APAR AREA KEC", true),
            Object("5", "LAMPU SOROT AREA KEC", true),
            Object("6", "PARKING SISTEM AREA PARKIRAN KR2 & KR4", true),
            Object("7", "HYDRANT AREA PARKIRAN KR2 (HYDRANT PARKIR B3)", false),
            Object("8", "APAR AREA PARKIRAN KR2 (APAR PARKIR B2)", true),
        )
        objectViewAdapter.setList(_object = objectList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onItemClicked(_object: Object) {
        findNavController().navigate(R.id.action_listObjectFragment_to_reportingFragment)

    }


}