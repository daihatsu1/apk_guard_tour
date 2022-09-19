/*
 *     Digital Patrol Guard
 *     ListReportFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:10 AM
 */

package ai.digital.patrol.ui.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.digital.patrol.databinding.FragmentListReportBinding
import ai.digital.patrol.data.entity.Report
import ai.digital.patrol.data.entity.ReportDetail
import ai.digital.patrol.ui.form.listener.OnReportClickListener
import ai.digital.patrol.ui.form.viewadapter.ReportDetailViewAdapter
import androidx.recyclerview.widget.LinearLayoutManager

class ListReportFragment : Fragment(), OnReportClickListener {

    companion object {
        fun newInstance() = ListReportFragment()
    }

    private var _binding: FragmentListReportBinding? = null
    private val reportViewAdapter = ReportDetailViewAdapter(this)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListReportBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerReport
        recyclerView.adapter = reportViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        setListReport()

        return binding.root
    }

    private fun setListReport() {
//        val reportList: List<Report> = listOf<Report>(
//            Report("1", "LAMPU AREA DALAM", "LAMPU MATI"),
//            Report("2", "LAMPU DISKO", "LAMPU MATI", false),
//            Report("3", "HYDRANT AREA KEC ( HYDRANT NO 23 )", "BOCOR"),
//            Report("4", "APAR AREA KEC", "BERKARAT", false),
//            Report("5", "LAMPU SOROT AREA KEC", "REDUP"),
//            Report("7", "HYDRANT AREA PARKIRAN KR2 (HYDRANT PARKIR B3)", "AIR REMBES"),
//        )
//        reportViewAdapter.setList(_report = reportList)
    }

    override fun onItemClicked(_reportDetail: ReportDetail) {
//        findNavController().navigate(R.id.action_listReportFragment_to_reportingFragment)

    }


}