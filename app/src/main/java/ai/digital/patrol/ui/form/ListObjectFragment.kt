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
import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.data.entity.ObjectPatrol
import ai.digital.patrol.data.entity.Report
import ai.digital.patrol.data.entity.ReportDetail
import ai.digital.patrol.databinding.FragmentListObjectBinding
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.form.listener.OnObjectClickListener
import ai.digital.patrol.ui.form.viewadapter.ObjectViewAdapter
import ai.digital.patrol.worker.SyncViewModel
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListObjectFragment : Fragment(), OnObjectClickListener {

    companion object {
        fun newInstance() = ListObjectFragment()
    }

    private var _binding: FragmentListObjectBinding? = null
    private val objectViewAdapter = ObjectViewAdapter(this)
    private val binding get() = _binding!!

    private val args: ListObjectFragmentArgs by navArgs()
    lateinit var checkpoint: Checkpoint
    lateinit var report: Report
    private var confirmationPatrolCheckCallback: DialogCallbackListener? = null
    private var confirmationPatrolCheckpointDoneCallback: DialogCallbackListener? = null
    private var clickBackPressed: Boolean = true
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this,
            PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }
    private val syncViewModel by lazy {
        ViewModelProvider(this)[SyncViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListObjectBinding.inflate(inflater, container, false)
        args.let {
            checkpoint = it.dataCheckpoint
            report = it.dataReport
        }
        _binding!!.titleObject.text = buildString {
            append("PILIH OBJECT CHECKPOINT ")
            append(checkpoint.check_name)
        }

        val recyclerView = binding.recyclerObject
        recyclerView.adapter = objectViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        setListObject()
        setCallbackCheckpointDone(checkpoint)
        _binding!!.fabCheckpointDone.setOnClickListener {
            dialogConfirmDoneCheck(checkpoint)
        }

        binding.titleReportListsBtn.setOnClickListener {
            val action =
                ListObjectFragmentDirections.actionObjectFragmentToListReportFragment(
                    checkpoint
                )
            findNavController().navigate(action)
        }
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (clickBackPressed) {
//                    dialogConfirmDoneCheck(checkpoint)
                } else {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, onBackPressedCallback as OnBackPressedCallback
        )

        return binding.root
    }


    private fun setListObject() {
        patrolDataViewModel.getObjectByCheckpoint(checkpoint.id)
            ?.observe(viewLifecycleOwner) { it ->
                objectViewAdapter.setList(it)
                val totalObject = it.size
                val totalObjectChecked = it.filter {
                    it.is_normal != null
                }.size
                if (totalObjectChecked < totalObject) {
                    binding.bottomLayout.visibility = GONE
                } else {
                    binding.bottomLayout.visibility = VISIBLE
                }
            }
    }


    override fun onItemClicked(_objectPatrol: ObjectPatrol) {
        if (_objectPatrol.nama_objek != null) {
            setCallback(_objectPatrol)
            dialogConfirmCheckObject(_objectPatrol.nama_objek)
        }
    }

    private fun dialogConfirmCheckObject(objectName: String) {
        val title = "APAKAH OBJECT INI DALAM KEADAAN NORMAL?"
        val subTitle = objectName
        val positiveText = "NORMAL"
        val negativeText = "TIDAK NORMAL"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationPatrolCheckCallback!!,
            R.color.success
        )
            .show(parentFragmentManager, "dialogConfirmCheckObject")
    }

    private fun dialogConfirmDoneCheck(_checkpoint: Checkpoint) {
        val title =
            "APAKAH ANDA YAKIN TELAH SELESAI PATORLI DI CHEKPOINT ${_checkpoint.check_name} ?"
        val subTitle = "Pastikan semua object telah di periksa."
        val positiveText = "SELESAI"
        val negativeText = "CEK KEMBALI"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationPatrolCheckpointDoneCallback!!,
            R.color.success
        ).show(parentFragmentManager, "dialogConfirmDoneCheck")
    }


    private fun setCallback(_objectPatrol: ObjectPatrol) {
        confirmationPatrolCheckCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                setNormalObject(_objectPatrol, report)
                dialog?.dismiss()
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()

                val action =
                    ListObjectFragmentDirections.actionListObjectFragmentToReportingFragment(
                        _objectPatrol, checkpoint, report
                    )
                findNavController().navigate(action)
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = "DAFTAR OBJEK PATROLI"

        syncViewModel.syncReportData()
        patrolDataViewModel.getDataTemuanByCheckpoint(checkpoint.id)?.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.titleReportListsBtn.visibility = VISIBLE
            } else {
                binding.titleReportListsBtn.visibility = GONE
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setNormalObject(_objectPatrol: ObjectPatrol, report: Report) {
        GlobalScope.launch(Dispatchers.IO) {
            val reportData =
                patrolDataViewModel.getReportByCheckpointId(checkpointId = checkpoint.id)
            if (reportData != null) {
                val dataReportDetail = ReportDetail(
                    admisecsgp_mstobj_objek_id = _objectPatrol.id,
                    is_laporan_kejadian = 0,
                    laporkan_pic = 0,
                    is_tindakan_cepat = 0,
                    conditions = "Normal",
                    description = "Normal",
                    status = 1,
                    status_temuan = 1,
                    created_at = Utils.createdAt("yyyy-MM-dd HH:mm:ss"),
                    synced = false,
                    reportId = reportData.sync_token,
                    admisecsgp_mstckp_checkpoint_id = checkpoint.id
                )
                patrolDataViewModel.addReportNormalDetail(reportData, dataReportDetail)
                syncViewModel.syncReportData()
            }
        }
    }

    private fun setCallbackCheckpointDone(_checkpoint: Checkpoint) {
        confirmationPatrolCheckpointDoneCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                // record checkpoint checkout time
                patrolDataViewModel.checkOutCheckpoint(_checkpoint.id)
                // navigate to list checkpoint
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

}