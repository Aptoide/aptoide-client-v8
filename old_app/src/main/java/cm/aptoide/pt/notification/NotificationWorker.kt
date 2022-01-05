package cm.aptoide.pt.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.sync.Sync
import cm.aptoide.pt.sync.SyncScheduler
import cm.aptoide.pt.sync.alarm.AlarmSyncScheduler
import cm.aptoide.pt.sync.alarm.SyncStorage

class NotificationWorker(context: Context, workerParameters: WorkerParameters,
                         private val scheduler: SyncScheduler,
                         private val storage: SyncStorage,
                         private val crashReport: CrashReport) :
    Worker(context, workerParameters) {


  override fun doWork(): Result {

    if (AlarmSyncScheduler.ACTION_SYNC == inputData.getString("action")) {
      val syncId = inputData.getString("sync_id")

      val sync: Sync = storage.get(syncId)
      val reschedule: Boolean =
          inputData.getBoolean(AlarmSyncScheduler.EXTRA_RESCHEDULE, false)
      if (sync != null) {
        sync.execute()
            .doOnTerminate {
              Log.d("CampaignWorker", "Got notification from $syncId")
              if (reschedule) {
                scheduler.reschedule(sync)
              }
            }
            .subscribe({}
            ) { throwable: Throwable? -> crashReport.log(throwable) }
      } else {
        scheduler.cancel(syncId)
      }
    }
    return Result.success()
  }
}