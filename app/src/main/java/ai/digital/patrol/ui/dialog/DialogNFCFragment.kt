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
import ai.digital.patrol.data.entity.Checkpoint
import ai.digital.patrol.databinding.FragmentDialogNfcBinding
import ai.digital.patrol.helper.AppEvent
import ai.digital.patrol.helper.EventBus
import ai.digital.patrol.helper.loadDrawable
import ai.digital.patrol.ui.form.MainFormActivity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable


/**
 * Dialog fragment
 *
 * @property dialogCallbackListener
 * @constructor Create empty Dialog fragment
 */
class DialogNFCFragment(
    private val dialogCallbackListener: DialogCallbackListener,
    val checkpoint: Checkpoint?
) :
    DialogFragment() {
    override fun getTheme() = R.style.RoundedCornersDialog
    companion object {
        const val TAG = "NFCDialogFragment"
        fun newInstance(dialogCallbackListener: DialogCallbackListener, checkpoint: Checkpoint?): ai.digital.patrol.ui.dialog.DialogNFCFragment {
            val args = Bundle()

            val fragment = DialogNFCFragment(dialogCallbackListener, checkpoint)
            fragment.arguments = args
            return fragment
        }

    }
    var binding: FragmentDialogNfcBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogNfcBinding.inflate(inflater, container, false)
        binding!!.dialogTitle.text = checkpoint?.check_name

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
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
    private fun setupView() {
        binding!!.dialogIcon.loadDrawable(R.drawable.icon_nfc)
        binding!!.dialogIcon.setOnClickListener {
//            dialogCallbackListener.onPositiveClickListener(it, dialog)
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

    override fun onResume() {
        super.onResume()
        if (checkpoint?.no_nfc!=null){
            (activity as MainFormActivity?)?.enableNFC(checkpoint.no_nfc) // enable read nfc
        }

    }

    override fun onPause() {
        super.onPause()
        (activity as MainFormActivity?)?.disableNFC() // enable read nfc
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}