/*
 *     Digital Patrol Guard
 *     DialogFragment.kt
 *     Created by ImamSyahrudin on 5/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/5/22, 10:35 AM
 */

package ai.digital.patrol.ui.dialog

import ai.digital.patrol.R
import ai.digital.patrol.databinding.FragmentDialogConfirmPatrolBinding
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
class DialogFragment(private val dialogCallbackListener: DialogCallbackListener) : DialogFragment() {
    override fun getTheme() = R.style.RoundedCornersDialog

    companion object {

        const val TAG = "DialogFragment"
        var binding: FragmentDialogConfirmPatrolBinding? = null

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private const val KEY_POSITIVE = "KEY_POSITIVE"
        private const val KEY_NEGATIVE = "KEY_NEGATIVE"

        fun newInstance(title: String, subTitle: String, positiveText: String, negativeText: String,
                        dialogCallbackListener: DialogCallbackListener): ai.digital.patrol.ui.dialog.DialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            args.putString(KEY_POSITIVE, positiveText)
            args.putString(KEY_NEGATIVE, negativeText)
            val fragment = DialogFragment(dialogCallbackListener)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogConfirmPatrolBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupClickListeners(view)
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

        binding!!.dialogTitle.text = arguments?.getString(KEY_TITLE)
        binding!!.dialogSubtitle.text = arguments?.getString(KEY_SUBTITLE)
        binding!!.btnDialogPositif.text = arguments?.getString(KEY_POSITIVE)
        binding!!.btnDialogNegative.text = arguments?.getString(KEY_NEGATIVE)
    }

    /**
     * Setup click listeners
     *
     * assign dialog callback listener
     *
     * @param view
     */
    private fun setupClickListeners(view: View) {
        binding!!.btnDialogPositif.setOnClickListener {
            dialogCallbackListener.onPositiveClickListener(it, dialog)
        }
        binding!!.btnDialogNegative.setOnClickListener {
            dialogCallbackListener.onNegativeClickListener(it, dialog)
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