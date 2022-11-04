/*
 *     Digital Patrol Guard
 *     ListReportFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:10 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.ReportDetail
import ai.digital.patrol.databinding.FragmentListReportBinding
import ai.digital.patrol.ui.form.listener.OnReportClickListener
import ai.digital.patrol.ui.form.viewadapter.TemuanViewAdapter
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager


class ListReportFragment : Fragment(), OnReportClickListener {

    companion object {
        fun newInstance() = ListReportFragment()
    }

    private var _binding: FragmentListReportBinding? = null
    private val temuanViewAdapter = TemuanViewAdapter(this)
    private val binding get() = _binding!!
    private val args: ListReportFragmentArgs by navArgs()
    lateinit var checkpoint: Checkpoint

    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this,
            PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListReportBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerReport
        recyclerView.adapter = temuanViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        checkpoint = args.dataCheckpoint!!
        _binding!!.titleReport.text = buildString {
            append("TEMUAN DI CHECKPOINT ")
            append(checkpoint.check_name)
        }
        setListReport(checkpoint)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.title = "DAFTAR TEMUAN CHECKPOINT"
    }

    private fun setListReport(checkpoint: Checkpoint) {
        patrolDataViewModel.getDataTemuanByCheckpoint(checkpoint.id)?.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                temuanViewAdapter.setList(it)
            }
        }
    }

    override fun onItemClicked(_reportDetail: ReportDetail) {
//        findNavController().navigate(R.id.action_listReportFragment_to_reportingFragment)

    }


}