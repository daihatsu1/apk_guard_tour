package ai.digital.patrol.worker

import ai.digital.patrol.data.entity.Report
import ai.digital.patrol.data.repository.PatrolDataRepository
import ai.digital.patrol.helper.Utils
import ai.digital.patrol.networking.ServiceGenerator
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SyncReportWorker
/**
 * Creates an instance of the [Worker].
 *
 * @param appContext   the application [Context]
 * @param workerParams the set of [WorkerParameters]
 */
    (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private val patrolDataRepository = PatrolDataRepository.getInstance()

    private fun toRequestBody(value: Any?): Any {
        return RequestBody.create(MediaType.parse("text/plain"), value.toString())
    }
    
    override fun doWork(): Result {
        Log.i(TAG, "SyncSurveyDataWorker START")
        val reportData = patrolDataRepository!!.getUnSyncReport()
        if (reportData.isEmpty()) {
            val output =
                Data.Builder().putString("reportData", "All Data Synced")
                    .build()
            return Result.success(output)
        }
        val postSurveyDataCall = postReport(reportData)
        val output =
            Data.Builder().putString("reportData", postSurveyDataCall.toString())
                .build()
        return Result.success(output)
    }
    private fun postReport(unSyncReport: List<Report>) {
        val restInterface = ServiceGenerator.createService()
        

        unSyncReport.forEachIndexed { k, it ->
            Log.d("COUNTER UPLOAD", k.toString())
            val map: MutableMap<String, RequestBody> = HashMap()
            map["admisecsgp_mstusr_npk"] = toRequestBody(it.admisecsgp_mstusr_npk) as RequestBody
            map["admisecsgp_mstshift_shift_id"] =
                toRequestBody(it.admisecsgp_mstshift_shift_id) as RequestBody
            map["admisecsgp_mstzone_zone_id"] =
                toRequestBody(it.admisecsgp_mstzone_zone_id) as RequestBody
            map["admisecsgp_mstckp_checkpoint_id"] =
                toRequestBody(it.admisecsgp_mstckp_checkpoint_id) as RequestBody
            map["date_patroli"] = toRequestBody(it.date_patroli) as RequestBody
            map["checkin_checkpoint"] = toRequestBody(it.checkin_checkpoint) as RequestBody
            map["checkout_checkpoint"] = toRequestBody(it.checkout_checkpoint) as RequestBody

            map["status"] = toRequestBody(it.status) as RequestBody

            map["sync_token"] = toRequestBody(it.sync_token) as RequestBody
            map["created_at"] = toRequestBody(it.created_at) as RequestBody
            it.detailTemuan?.forEachIndexed { index, its ->
                map["detail_temuan[$index][admisecsgp_mstobj_objek_id]"] =
                    toRequestBody(its.admisecsgp_mstobj_objek_id) as RequestBody
                map["detail_temuan[$index][conditions]"] =
                    toRequestBody(0) as RequestBody
                map["detail_temuan[$index][admisecsgp_mstevent_event_id]"] =
                    toRequestBody(its.admisecsgp_mstevent_event_id) as RequestBody
                map["detail_temuan[$index][description]"] =
                    toRequestBody(its.description) as RequestBody
                map["detail_temuan[$index][is_laporan_kejadian]"] =
                    toRequestBody(its.is_laporan_kejadian) as RequestBody
                map["detail_temuan[$index][laporkan_pic]"] =
                    toRequestBody(its.laporkan_pic) as RequestBody
                map["detail_temuan[$index][is_tindakan_cepat]"] =
                    toRequestBody(its.is_tindakan_cepat) as RequestBody
                map["detail_temuan[$index][deskripsi_tindakan]"] =
                    toRequestBody(its.deskripsi_tindakan) as RequestBody

                map["detail_temuan[$index][note_tindakan_cepat]"] =
                    toRequestBody(its.note_tindakan_cepat) as RequestBody

                map["detail_temuan[$index][status]"] =
                    Utils.toRequestBody(its.status) as RequestBody

                map["detail_temuan[$index][created_at]"] =
                    toRequestBody(its.created_at) as RequestBody
                map["detail_temuan[$index][sync_token]"] =
                    toRequestBody(its.sync_token) as RequestBody

                if (its.image_1 != null && its.image_1 != "null" && !its.image_1.startsWith("http")) {
                    val uri = Uri.parse(its.image_1)
                    if (uri != null) {
                        val file = uri.path?.let { it1 -> File(it1) }
                        if (file != null) {
                            Log.d("FILE", file.path)
                            val filename = file.name
                            val key =
                                "detail_temuan[$index][image_1]\"; filename=\"ap_$filename"
                            val fileBody = RequestBody.create(MediaType.parse("image/*"), file)
                            map[key] = fileBody
                        }
                    }
                }
                if (its.image_2 != null && its.image_2 != "null" && !its.image_2.startsWith("http")) {
                    val uri = Uri.parse(its.image_2)
                    if (uri != null) {
                        val file = uri.path?.let { it1 -> File(it1) }
                        if (file != null) {
                            val filename = file.name
                            val key = "detail_temuan[$index][image_2]\"; filename=\"ap_$filename"
                            val fileBody = RequestBody.create(MediaType.parse("image/*"), file)
                            map[key] = fileBody
                        }

                    }
                }
                if (its.image_3 != null && its.image_3 != "null" && !its.image_3.startsWith("http")) {
                    val uri = Uri.parse(its.image_3)
                    if (uri != null) {
                        val file = uri.path?.let { it1 -> File(it1) }
                        if (file != null) {
                            val filename = file.name
                            val key =
                                "detail_temuan[$index][image_3]\"; filename=\"ap_$filename"
                            val fileBody = RequestBody.create(MediaType.parse("image/*"), file)
                            map[key] = fileBody
                        }
                    }
                }
            }
            val upload = restInterface.postReport(map)
            upload.enqueue(object : Callback<Report> {
                override fun onResponse(
                    call: Call<Report>,
                    response: Response<Report>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val report = response.body()
                            if (report != null) {
                                report.synced = true
                                report.sync_token = it.sync_token
                                patrolDataRepository?.syncReport(report, report.detailTemuan)
                               
                            }
                        }
                    } else {
                        Log.d("Upload", "Gagal")
                    }
                }

                override fun onFailure(call: Call<Report>, t: Throwable) {
                    Log.d("Upload", "Error " + t.message)
                    t.printStackTrace()
                }
            })

        }

    }
    companion object {
        private val TAG = SyncReportWorker::class.java.simpleName
    }
}
