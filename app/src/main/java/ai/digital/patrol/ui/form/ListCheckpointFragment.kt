/*
 *     Digital Patrol Guard
 *     ListCheckpointFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 10:42 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.R
import ai.digital.patrol.databinding.FragmentListCheckpointBinding
import ai.digital.patrol.model.Checkpoint
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.dialog.DialogNFCFragment
import ai.digital.patrol.ui.form.listener.OnCheckpointClickListener
import ai.digital.patrol.ui.form.viewadapter.CheckpointViewAdapter
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ListCheckpointFragment : Fragment(), OnCheckpointClickListener {

    private var _binding: FragmentListCheckpointBinding? = null
    private val checkpointViewAdapter = CheckpointViewAdapter(this)
    private var nfcCallback: DialogCallbackListener? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListCheckpointBinding.inflate(inflater, container, false)
        val recyclerView = binding.recyclerCheckpoint
        recyclerView.adapter = checkpointViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        setListCheckpoint()
        nfcCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
                findNavController().navigate(R.id.action_CheckpointFragment_to_listObjectFragment)
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
                dialog.dismiss()
            }
        }
        return binding.root

    }

    private fun dialogNfc() {
        DialogNFCFragment.newInstance(nfcCallback!!)
            .show(parentFragmentManager, "dialogConfirmStartPatrol")
    }

    private fun setListCheckpoint() {
        val checkpoint: List<Checkpoint> = listOf<Checkpoint>(
            Checkpoint("1", "1", "PLANT 5", "FACTORY_PRESS UTARA", "2817214131"),
            Checkpoint("2", "1", "PLANT 5", "FACTORY_PRESS SELATAN", "2817214131"),
            Checkpoint("3", "1", "PLANT 5", "FACTORY_ WELDING OFFICE CKD", "2817214131"),
            Checkpoint("4", "1", "PLANT 5", "FACTORY_ASSEMBLING OFFICE LOG", "2817214131"),
            Checkpoint("5", "1", "PLANT 5", "FACTORY_ASSEMBLING BENCHTRY", "2817214131"),
        )
        checkpointViewAdapter.setList(checkpoint)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogNfc()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(_checkpoint: Checkpoint) {
        dialogNfc()
    }
}