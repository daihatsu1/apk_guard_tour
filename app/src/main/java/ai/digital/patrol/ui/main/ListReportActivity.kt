/*
 *     Digital Patrol Guard
 *     ListReportActivity.kt
 *     Created by ImamSyahrudin on 13/9/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 9/13/22, 10:29 AM
 */

package ai.digital.patrol.ui.main

import ai.digital.patrol.R
import ai.digital.patrol.databinding.ActivityListReportBinding
import ai.digital.patrol.ui.form.PatrolDataViewModel
import ai.digital.patrol.ui.form.viewadapter.ReportDetailHomeViewAdapter
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager

class ListReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListReportBinding
    private val reportHomeViewAdapter = ReportDetailHomeViewAdapter(null)
    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this,
            PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        title = "Daftar Temuan Hari Ini"
        val recyclerHomeViewReporting = binding.layoutList.recyclerReportList
        recyclerHomeViewReporting.adapter = reportHomeViewAdapter
        recyclerHomeViewReporting.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.layoutList.fabBackHome.setOnClickListener {
            finish()
        }
        setListReport()
    }
    private fun setListReport() {

        patrolDataViewModel.getReportDetail()?.observe(this) { it ->
            reportHomeViewAdapter.setList(it)

        }
    }
}