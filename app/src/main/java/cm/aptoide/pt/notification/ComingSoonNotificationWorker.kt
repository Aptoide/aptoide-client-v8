package cm.aptoide.pt.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import cm.aptoide.pt.AptoideApplication
import cm.aptoide.pt.DeepLinkIntentReceiver
import cm.aptoide.pt.R
import cm.aptoide.pt.app.aptoideinstall.ComingSoonApp
import cm.aptoide.pt.logger.Logger
import cm.aptoide.pt.view.app.AppCenter
import cm.aptoide.pt.view.app.DetailedAppRequestResult

class ComingSoonNotificationWorker(private val context: Context,
                                   private val workerParameters: WorkerParameters,
                                   private val appCenter: AppCenter) :
    Worker(context, workerParameters) {

  override fun doWork(): Result {
    val packageName = inputData.getString(ComingSoonNotificationManager.PACKAGE_NAME)

    appCenter.loadDetailedApp(packageName, "catappult")
        .doOnSuccess { Logger.getInstance().d("lol", "got the result from load app") }
        .doOnSuccess { detailedAppResult: DetailedAppRequestResult? ->
          if (detailedAppResult != null && detailedAppResult.detailedApp != null) {
            cancelComingSoonVerification(packageName)
            handleAppArrived(ComingSoonApp(
                detailedAppResult.detailedApp.name,
                detailedAppResult.detailedApp.icon, detailedAppResult.detailedApp.md5,
                detailedAppResult.detailedApp.store.name
            ))
          }
        }.toBlocking().value()
    return Result.success()
  }

  private fun handleAppArrived(comingSoonApp: ComingSoonApp) {
    Logger.getInstance().d("lol", "going to show the notification")
    val resultIntent = Intent(applicationContext,
        AptoideApplication.getActivityProvider()
            .mainActivityFragmentClass)
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES, true)
    val resultPendingIntent =
        PendingIntent.getActivity(applicationContext, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(context, ComingSoonNotificationManager.CHANNEL_ID)
        .setContentIntent(
            resultPendingIntent)
        .setOngoing(false)
        .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
        .setContentTitle("aptoide")
        .setContentText("aptoide has arrived! ")
        .setAutoCancel(true).build()

    with(NotificationManagerCompat.from(context)) {
      notify(ComingSoonNotificationManager.NOTIFICATION_ID, notification)
    }

  }

  private fun cancelComingSoonVerification(packageName: String?) {
    Logger.getInstance().d("lol", "canceling the coming soon verification")
    if (packageName != null) {
      WorkManager.getInstance(context).cancelAllWorkByTag(packageName)
    }
  }
}