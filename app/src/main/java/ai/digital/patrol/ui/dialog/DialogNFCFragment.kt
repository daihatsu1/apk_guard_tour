/*
 *     Digital Patrol Guard
 *     DialogNFCFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/8/22, 2:31 AM
 */

package ai.digital.patrol.ui.dialog

import ai.digital.patrol.R
import ai.digital.patrol.databinding.FragmentDialogConfirmPatrolBinding
import ai.digital.patrol.databinding.FragmentDialogNfcBinding
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment


/**
 * Dialog fragment
 *
 * @property dialogCallbackListener
 * @constructor Create empty Dialog fragment
 */
class DialogNFCFragment(private val dialogCallbackListener: DialogCallbackListener) : DialogFragment() {
    override fun getTheme() = R.style.RoundedCornersDialog

    companion object {

        const val TAG = "DialogFragment"
        var binding: FragmentDialogNfcBinding? = null

        fun newInstance(dialogCallbackListener: DialogCallbackListener): ai.digital.patrol.ui.dialog.DialogNFCFragment {
            val args = Bundle()

            val fragment = DialogNFCFragment(dialogCallbackListener)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogNfcBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    /**
     * On start
     * set dialog width and height
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Setup view for dialog
     *
     * @param view
     */
    private fun setupView(view: View) {

        binding!!.dialogIcon.setImageResource(R.drawable.icon_nfc)
        binding!!.dialogIcon.setOnClickListener{
            dialogCallbackListener.onPositiveClickListener(it, dialog)
        }

    }


    /**
     * On dismiss
     *
     * assign dialog onDismiss listener
     *
     * @param dialog
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogCallbackListener.onDismissClickListener(dialog)

    }
}