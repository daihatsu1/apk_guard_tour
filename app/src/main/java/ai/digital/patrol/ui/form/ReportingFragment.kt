/*
 *     Digital Patrol Guard
 *     ReportingFragment.kt
 *     Created by ImamSyahrudin on 8/8/2022
 *     Copyright © 2022 imamSyahrudin. All rights reserved.
 *
 *     Last modified 8/6/22, 1:05 AM
 */

package ai.digital.patrol.ui.form

import ai.digital.patrol.data.entity.*
import ai.digital.patrol.databinding.FragmentReportingBinding
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.ui.dialog.DialogCallbackListener
import ai.digital.patrol.ui.dialog.DialogFragment
import ai.digital.patrol.ui.form.listener.OnPhotoClickListener
import ai.digital.patrol.ui.form.viewadapter.PhotoViewAdapter
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReportingFragment : Fragment(), OnPhotoClickListener {

    private lateinit var _binding: FragmentReportingBinding
    private val args: ReportingFragmentArgs by navArgs()
    private lateinit var _objectPatrol: ObjectPatrol
    private lateinit var _checkpoint: Checkpoint
    private lateinit var _report: Report
    private val patrolDataViewModel by lazy {
        ViewModelProvider(
            this,
            PatrolDataViewModel.Factory(Application())
        )[PatrolDataViewModel::class.java]
    }
    private val imageList: ArrayList<PhotoReport> = ArrayList()
    private val photoViewAdapter = PhotoViewAdapter(this)
    private var selectedEvent: KeyValueModel? = null
    private var confirmationSubmitCallback: DialogCallbackListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportingBinding.inflate(inflater, container, false)
        _objectPatrol = args.dataObject
        _checkpoint = args.dataCheckpoint
        _report = args.dataReport

        _binding.title.text = _objectPatrol.nama_objek
        _binding.btnReportingSave.setOnClickListener {
            if (isValid()) dialogConfirmSubmit()
        }
        _objectPatrol = args.dataObject!!

        _binding.etReportingNote.isValid("Harus di isi") { s -> s.isNotEmpty() }
        _binding.etReportingCategory.isValid("Harus di isi") { s -> s.isNotEmpty() }
        val recyclerPhoto = _binding.recyclerPhoto
        recyclerPhoto.adapter = photoViewAdapter
        recyclerPhoto.layoutManager = GridLayoutManager(this.requireContext(), 2)
        setListEvent()
        setReportSubmitDialogFragmentListener()
        val previewImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val uri: Uri = data?.data!!
                        imageList.add(PhotoReport(uri))
                        photoViewAdapter.setList(imageList)
                        if (imageList.size >= 3) {
                            _binding.btnAddPhoto.visibility = GONE
                        } else {
                            _binding.btnAddPhoto.visibility = VISIBLE
                        }
                    }

                    ImagePicker.RESULT_ERROR -> {
                        Log.e("RESULT_ERROR", ImagePicker.getError(data))
                    }

                    else -> {
                        Toast.makeText(
                            this.requireContext(),
                            "Tambah Foto Dibatalkan",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        _binding.btnAddPhoto.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .saveDir(
                    this@ReportingFragment.requireContext()
                        .getExternalFilesDir(Environment.DIRECTORY_DCIM)!!
                ).cameraOnly()
                .saveDir(requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)!!)
                .createIntent { intent ->
                    previewImageResult.launch(intent)
                }
        }
        _binding.cbReportingQuickAction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                _binding.tilReportingActionNote.visibility = VISIBLE
            } else {
                _binding.tilReportingActionNote.visibility = GONE
            }
        }

        return _binding.root
    }

    private fun setListEvent() {
        patrolDataViewModel.getEventByObject(_objectPatrol.id)
            ?.observe(viewLifecycleOwner) { it ->
                Log.d("eventList", it.toString())
                val eventList: MutableList<KeyValueModel> = ArrayList()
                it.forEach {
                    eventList.add(KeyValueModel(it.id, it.event_name))
                }
                val adapterCategory = ArrayAdapter(
                    this.requireContext(),
                    android.R.layout.simple_list_item_1,
                    eventList
                )
                _binding.etReportingCategory.setAdapter(adapterCategory)
                _binding.etReportingCategory.onItemClickListener =
                    AdapterView.OnItemClickListener { _: AdapterView<*>, _: View, i: Int, l: Long ->
                        selectedEvent = adapterCategory.getItem(i)!!
                        Log.d("selectedEvent", eventList[i].value.toString())
                        Log.d("selectedEvent", eventList[i].key.toString())
                    }
            }
    }

    private fun isValid(): Boolean {
        if (selectedEvent?.value.toString().isEmpty()) {
            _binding.tilReportingCategory.error = "WAJIB DI ISI"
            _binding.tilReportingActionNote.requestFocus()
            return false
        }

        if (_binding.etReportingNote.text.toString().isEmpty()) {
            _binding.tilReportingNote.error = "WAJIB DI ISI"
            _binding.tilReportingNote.requestFocus()
            return false
        }

        if (_binding.cbReportingQuickAction.isChecked) {
            if (_binding.etReportingActionNote.text.toString().isEmpty()) {
                _binding.tilReportingActionNote.error = "WAJIB DI ISI"
                _binding.tilReportingActionNote.requestFocus()
                return false
            }
        }

        if (_binding.radioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Wajib Pilih Tindakan", Toast.LENGTH_SHORT).show()
            return false
        }

        if (imageList.isEmpty()) {
            _binding.btnAddPhoto.requestFocus()
            _binding.btnAddPhoto.error = "Wajib Tambahkan Foto."
            Toast.makeText(context, "Wajib Tambahkan Foto.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun collectData() {
        GlobalScope.launch(Dispatchers.IO) {
            val reportData =
                patrolDataViewModel.getReportByCheckpointId(checkpointId = _checkpoint.id)
            if (reportData != null) {
                val category = selectedEvent?.key.toString()
                val categoryText = selectedEvent?.value.toString()
                val note = _binding.etReportingNote.text.toString()
                val actionNote = _binding.etReportingActionNote.text.toString()
                val picReport = if (_binding.cbReportingPic.isChecked) 1 else 0
                val quickAction = if (_binding.cbReportingQuickAction.isChecked) 1 else 0
                val accident = 0

                if (isValid()) {
                    val image1 = imageList.getOrNull(0)?.photoUri.toString()
                    val image2 = imageList.getOrNull(1)?.photoUri.toString()
                    val image3 = imageList.getOrNull(2)?.photoUri.toString()

                    val dataReportDetail = ReportDetail(
                        admisecsgp_mstobj_objek_id = _objectPatrol.id,
                        conditions = categoryText,
                        admisecsgp_mstevent_event_id = category,
                        description = note,
                        image_1 = image1,
                        image_2 = image2,
                        image_3 = image3,
                        is_laporan_kejadian = accident,
                        laporkan_pic = picReport,
                        is_tindakan_cepat = quickAction,
                        note_tindakan_cepat = actionNote,
                        created_at = Utils.createdAt("yyyy-MM-dd HH:mm:ss"),
                        synced = false,
                        reportId = reportData.sync_token
                    )
                    if (quickAction == 1) {
                        dataReportDetail.status = 0
                        dataReportDetail.status_temuan = 1
                        dataReportDetail.is_tindakan_cepat = quickAction
                        dataReportDetail.note_tindakan_cepat = actionNote
                    } else {
                        dataReportDetail.status = 0
                        dataReportDetail.status_temuan = 0
                    }
                    Log.d("ReportDetail", dataReportDetail.toString())
                    patrolDataViewModel.addReportDetail(_report, dataReportDetail)
                }
            }
        }
    }

    private fun dialogConfirmSubmit() {
        val title = "PASTIKAN DATA YANG DI MASUKAN TELAH BENAR?"
        val subTitle = ""
        val positiveText = "SIMPAN"
        val negativeText = "CEK KEMBALI"

        DialogFragment.newInstance(
            title,
            subTitle,
            positiveText,
            negativeText,
            confirmationSubmitCallback!!
        ).show(parentFragmentManager, "dialogConfirmReportSubmit")
    }

    private fun setReportSubmitDialogFragmentListener() {
        confirmationSubmitCallback = object : DialogCallbackListener {
            override fun onPositiveClickListener(v: View, dialog: Dialog?) {
                if (isValid()) {
                    collectData()
                    activity?.onBackPressed()
                    dialog?.dismiss()
                }
            }

            override fun onNegativeClickListener(v: View, dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onDismissClickListener(dialog: DialogInterface) {
            }
        }
    }

    override fun onPhotoDeleteBtnClick(photoReport: PhotoReport) {
        imageList.removeIf {
            it.photoUri == photoReport.photoUri
        }
        photoViewAdapter.notifyDataSetChanged()
        if (imageList.size >= 3) {
            _binding.btnAddPhoto.visibility = GONE
        } else {
            _binding.btnAddPhoto.visibility = VISIBLE
        }
    }
}


/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.isValid(message: String, validator: (String) -> Boolean) {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.error = if (validator(this.text.toString())) null else message
}
