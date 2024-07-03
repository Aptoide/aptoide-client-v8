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
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.view.app.AppCenter
import cm.aptoide.pt.view.app.DetailedAppRequestResult

class ComingSoonNotificationWorker(context: Context,
                                   workerParameters: WorkerParameters,
                                   private val appCenter: AppCenter) :
    Worker(context, workerParameters) {

  private fun handleAppArrived(comingSoonApp: ComingSoonApp) {
    val resultIntent = Intent(applicationContext,
        AptoideApplication.getActivityProvider()
            .mainActivityFragmentClass)
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true)
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, comingSoonApp.md5)
    val resultPendingIntent =
        PendingIntent.getActivity(applicationContext, 0, resultIntent,
          PendingIntent.FLAG_IMMUTABLE )

    val notificationBody: String =
        applicationContext.getString(R.string.promotional_new_notification_body,
            comingSoonApp.appName)

    val notification =
        NotificationCompat.Builder(applicationContext, ComingSoonNotificationManager.CHANNEL_ID)
            .setContentIntent(
                resultPendingIntent)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
            .setLargeIcon(ImageLoader.with(applicationContext)
                .loadBitmap(comingSoonApp.appIcon))
            .setContentTitle(
                applicationContext.getString(R.string.promotional_new_notification_title))
            .setContentText(notificationBody)
            .setAutoCancel(true).build()

    with(NotificationManagerCompat.from(applicationContext)) {
      notify(ComingSoonNotificationManager.NOTIFICATION_ID, notification)
    }

  }

  private fun cancelComingSoonVerification(packageName: String?) {
    if (packageName != null) {
      WorkManager.getInstance(applicationContext)
          .cancelAllWorkByTag(ComingSoonNotificationManager.WORKER_TAG + packageName)
    }
  }

  override fun doWork(): Result {
    val packageName = inputData.getString(ComingSoonNotificationManager.PACKAGE_NAME)
    appCenter.loadDetailedApp(packageName, "catappult")
        .doOnSuccess { detailedAppResult: DetailedAppRequestResult? ->
          if (detailedAppResult != null && detailedAppResult.detailedApp != null) {
            cancelComingSoonVerification(packageName)
            handleAppArrived(ComingSoonApp(
                detailedAppResult.detailedApp.name,
                detailedAppResult.detailedApp.icon, detailedAppResult.detailedApp.md5,
                detailedAppResult.detailedApp.store.name, detailedAppResult.detailedApp.packageName
            ))
          }
        }.toBlocking().value()
    return Result.success()
  }
}