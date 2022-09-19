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
import ai.digital.patrol.databinding.FragmentListObjectBinding
import ai.digital.patrol.data.entity.ObjectPatrol
import ai.digital.patrol.data.entity.Report
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.form.listener.OnObjectClickListener
import ai.digital.patrol.ui.form.viewadapter.ObjectViewAdapter
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager

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
        ViewModelProvider(this, PatrolDataViewModel.Factory(Application())).get(
            PatrolDataViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListObjectBinding.inflate(inflater, container, false)
        checkpoint = args.dataCheckpoint!!
        report = args.dataReport!!

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
            findNavController().navigate(R.id.action_object_fragment_to_listReportFragment)
        }
        onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(clickBackPressed){
                    dialogConfirmDoneCheck(checkpoint)
                }else{
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
            }
    }


    override fun onItemClicked(_objectPatrol: ObjectPatrol) {
        setCallback(_objectPatrol)
        _objectPatrol.nama_objek?.let { dialogConfirmCheckObject(it) }
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