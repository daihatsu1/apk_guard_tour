package ai.digital.patrol.worker

import ai.digital.patrol.helper.Cons.SCHEDULE_SYNC_WORKER_REPORT
import ai.digital.patrol.helper.Cons.SYNC_WORKER_NAME
import ai.digital.patrol.helper.Cons.SYNC_WORKER_REPORT
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import java.util.concurrent.TimeUnit

class SyncViewModel(application: Application) : AndroidViewModel(application) {
    private val mWorkManager: WorkManager = WorkManager.getInstance(application)

    val syncWorkInfo: LiveData<List<WorkInfo>> =
        mWorkManager.getWorkInfosForUniqueWorkLiveData(SYNC_WORKER_NAME)

    fun syncReportData() {
        Log.d(SYNC_WORKER_NAME, "syncing on proses" )
        cancelReportDataScheduleSyncWork()
        mWorkManager.pruneWork()
        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workerRequest = OneTimeWorkRequestBuilder<SyncReportWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        mWorkManager
            .enqueueUniqueWork(
                SYNC_WORKER_REPORT,
                ExistingWorkPolicy.APPEND,
                workerRequest
            )
    }


    fun cancelSyncWork() {
        mWorkManager.cancelUniqueWork(SYNC_WORKER_NAME)
    }

    private fun cancelReportDataScheduleSyncWork() {
        mWorkManager.cancelUniqueWork(SCHEDULE_SYNC_WORKER_REPORT)
    }

    private fun enqueueScheduleSyncReportDataWork() {
        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val scheduleSurveyDataSyncRequest = PeriodicWorkRequest.Builder(
            ScheduleReportDataSyncWorker::class.java, 15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        mWorkManager.enqueueUniquePeriodicWork(
            SCHEDULE_SYNC_WORKER_REPORT,
            ExistingPeriodicWorkPolicy.KEEP,
            scheduleSurveyDataSyncRequest
        )
    }


    init {
        enqueueScheduleSyncReportDataWork()
    }
}