package ai.digital.patrol.worker;



import static ai.digital.patrol.helper.Cons.SCHEDULE_SYNC_WORKER_REPORT;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Cleans up temporary files generated during blurring process
 */
public class ScheduleReportDataSyncWorker extends Worker {

    /**
     * Creates an instance of the {@link Worker}.
     *
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */
    public ScheduleReportDataSyncWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    private static final String TAG = ScheduleReportDataSyncWorker.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "ScheduleSyncWorker START");
        scheduleSync();
        Log.i(TAG, "ScheduleSyncWorker END");
        return Result.success();
    }

    private void scheduleSync() {
        Context applicationContext = getApplicationContext();
        WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(SCHEDULE_SYNC_WORKER_REPORT,
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.from(SyncReportWorker.class));
    }
}
