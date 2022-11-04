/*
 *     Digital Patrol Guard
 *     DialogFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.dialog

import ai.digital.patrol.R
import ai.digital.patrol.databinding.FragmentDialogConfirmPatrolBinding
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager


/**
 * Dialog fragment
 *
 * @property dialogCallbackListener
 * @constructor Create empty Dialog fragment
 */
class DialogFragment(private val dialogCallbackListener: DialogCallbackListener) :
    DialogFragment() {
    override fun getTheme() = R.style.RoundedCornersDialog

    companion object {

        const val TAG = "DialogFragment"
        var binding: FragmentDialogConfirmPatrolBinding? = null

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private const val KEY_POSITIVE = "KEY_POSITIVE"
        private const val KEY_POSITIVE_COLOR = "KEY_POSITIVE_COLOR"
        private const val KEY_NEGATIVE = "KEY_NEGATIVE"
        private const val KEY_SHOW_LIST = "SHOW_LIST"
        private const val KEY_DATA_LIST = "SHOW_DATA_LIST"
        private const val KEY_CANCELLABLE = "KEY_CANCELLABLE"

        fun newInstance(
            title: String,
            subTitle: String,
            positiveText: String,
            negativeText: String?,
            dialogCallbackListener: DialogCallbackListener,
            positiveColorInt: Int = R.color.primaryDarkColor,
            showList: Boolean = false,
            dataList: ArrayList<String>? = null,
            cancelable: Boolean = false
        ): ai.digital.patrol.ui.dialog.DialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            args.putString(KEY_POSITIVE, positiveText)
            args.putInt(KEY_POSITIVE_COLOR, positiveColorInt)
            args.putString(KEY_NEGATIVE, negativeText)
            args.putBoolean(KEY_SHOW_LIST, showList)
            args.putStringArrayList(KEY_DATA_LIST, dataList)
            args.putBoolean(KEY_CANCELLABLE, cancelable)
            val fragment = DialogFragment(dialogCallbackListener)
            fragment.arguments = args
            return fragment
        }

    }

    private val listDialogViewAdapter = ListDialogViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogConfirmPatrolBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val cancelable = arguments?.getBoolean(KEY_CANCELLABLE)
        if (cancelable == true) {
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
        }
        return dialog

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
        if (arguments?.getInt(KEY_POSITIVE_COLOR) != null) {
            binding!!.btnDialogPositif.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    arguments?.getInt(KEY_POSITIVE_COLOR)!!
                )
        }
        binding!!.dialogTitle.text = arguments?.getString(KEY_TITLE)
        if(arguments?.getString(KEY_SUBTITLE)?.isNotEmpty() == true){
            binding!!.dialogSubtitle.visibility = VISIBLE
            binding!!.dialogSubtitle.text = arguments?.getString(KEY_SUBTITLE)
        }else{
            binding!!.dialogSubtitle.visibility = GONE
        }
        binding!!.btnDialogPositif.text = arguments?.getString(KEY_POSITIVE)
        if (arguments?.getString(KEY_NEGATIVE) != null) {
            binding!!.btnDialogNegative.visibility = VISIBLE
            binding!!.btnDialogNegative.text = arguments?.getString(KEY_NEGATIVE)
        } else {
            binding!!.btnDialogNegative.visibility = GONE
        }
        if (arguments?.getBoolean(KEY_SHOW_LIST) == true) {
            binding!!.layoutDialogList.visibility = VISIBLE
            binding!!.rvListDialog.visibility = VISIBLE
            binding!!.dialogListTitle.visibility = VISIBLE
            val recyclerHomeViewReporting = binding!!.rvListDialog
            recyclerHomeViewReporting.adapter = listDialogViewAdapter
            recyclerHomeViewReporting.layoutManager =
                GridLayoutManager(context, 2)
            requireArguments().getStringArrayList(KEY_DATA_LIST)
                ?.let { listDialogViewAdapter.setList(it) }
        } else {
            binding!!.layoutDialogList.visibility = GONE
        }
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