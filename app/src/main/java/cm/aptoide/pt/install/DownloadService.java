/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.install;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BaseService;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.AppInBackgroundTracker;
import cm.aptoide.pt.R;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.notification.NotificationProvider;
import java.util.Locale;
import javax.inject.Inject;

public class DownloadService extends BaseService implements DownloadsNotification {

  public static final String TAG = "InstallService";

  public static final String ACTION_STOP_INSTALL = "STOP_INSTALL";
  public static final String ACTION_INSTALL_FINISHED = "INSTALL_FINISHED";
  public static final String EXTRA_INSTALLATION_MD5 = "INSTALLATION_MD5";
  static public final int PROGRESS_MAX_VALUE = 100;
  public static final String DOWNLOAD_APP_ACTION = "DOWNLOAD_APP";
  private static final int NOTIFICATION_ID = 8;
  private final int PAUSE_DOWNLOAD_REQUEST_CODE = 111;
  private final int OPEN_DOWNLOAD_MANAGER_REQUEST_CODE = 222;
  private final int OPEN_APPVIEW_REQUEST_CODE = 333;
  @Inject AptoideDownloadManager downloadManager;
  @Inject AppInBackgroundTracker appInBackgroundTracker;
  @Inject NotificationProvider notificationProvider;
  private DownloadsNotificationsPresenter downloadsNotificationsPresenter;
  private InstallManager installManager;
  private Notification progressNotification;

  private final String CHANNEL_ID = "download_notification_channel";

  public static Intent newInstanceForDownloads(Context context) {
    Intent intent = new Intent(context, DownloadService.class);
    intent.setAction(DOWNLOAD_APP_ACTION);
    return intent;
  }

  @Override public void onCreate() {
    super.onCreate();
    getApplicationComponent().inject(this);
    Logger.getInstance()
        .d(TAG, "Install service is starting");
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    installManager = application.getInstallManager();
    downloadsNotificationsPresenter =
        new DownloadsNotificationsPresenter(this, installManager, appInBackgroundTracker,
            notificationProvider);
    downloadsNotificationsPresenter.present();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {

      if (ACTION_STOP_INSTALL.equals(intent.getAction())) {
        String md5 = intent.getStringExtra(EXTRA_INSTALLATION_MD5);
        Logger.getInstance()
            .d(TAG, "received intent pausing download: " + md5);
        pauseDownload(md5);
      }
    }
    return START_STICKY;
  }

  @Override public void onDestroy() {
    Logger.getInstance()
        .d(this.getClass()
            .getName(), "InstallService.onDestroy");
    super.onDestroy();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private void pauseDownload(String md5) {
    progressNotification = null;
    downloadManager.pauseDownload(md5)
        .subscribe();
  }

  private NotificationCompat.Action getAction(int icon, String title, PendingIntent pendingIntent) {
    return new NotificationCompat.Action(icon, title, pendingIntent);
  }

  @NonNull private NotificationCompat.Action getPauseAction(String md5) {
    return getAction(cm.aptoide.pt.downloadmanager.R.drawable.media_pause,
        getString(cm.aptoide.pt.downloadmanager.R.string.pause_download),
        getPausePendingIntent(md5));
  }

  @NonNull private NotificationCompat.Action getDownloadManagerAction(int requestCode) {
    return getAction(R.drawable.ic_manager, getString(R.string.open_apps_manager),
        getOpenDownloadManagerPendingIntent(requestCode));
  }

  private PendingIntent getPausePendingIntent(String md5) {

    Intent intent = new Intent(this, DownloadService.class);
    if (!TextUtils.isEmpty(md5)) {
      final Bundle bundle = new Bundle();
      bundle.putString(EXTRA_INSTALLATION_MD5, md5);
      intent.putExtras(bundle);
    }
    intent.setAction(ACTION_STOP_INSTALL);
    return PendingIntent.getService(this, PAUSE_DOWNLOAD_REQUEST_CODE, intent,
        PendingIntent.FLAG_IMMUTABLE);
  }

  private PendingIntent getOpenDownloadManagerPendingIntent(int requestCode) {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION, true);
    return PendingIntent.getActivity(this, OPEN_DOWNLOAD_MANAGER_REQUEST_CODE, intent,
        PendingIntent.FLAG_IMMUTABLE);
  }

  private Notification buildNotification(String appName, int progress, boolean isIndeterminate,
      NotificationCompat.Action pauseAction, NotificationCompat.Action openDownloadManager,
      PendingIntent contentIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createNotificationChannel();
    }
    NotificationCompat.Builder builder = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      builder = new NotificationCompat.Builder(this, CHANNEL_ID);
    }else{
      builder = new NotificationCompat.Builder(this);
    }

    builder.setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle(String.format(Locale.ENGLISH,
            getResources().getString(cm.aptoide.pt.downloadmanager.R.string.aptoide_downloading),
            getString(R.string.app_name)))
        .setContentText(new StringBuilder().append(appName)
            .append(" - ")
            .append(getString(cm.aptoide.pt.database.R.string.download_progress)))
        .setContentIntent(contentIntent)
        .setProgress(PROGRESS_MAX_VALUE, progress, isIndeterminate)
        .addAction(pauseAction)
        .addAction(openDownloadManager);
    return builder.build();
  }
  @RequiresApi(Build.VERSION_CODES.O)
  private NotificationChannel getChannel() {
    String name = "Download notifications";
    String descriptionText = "Download";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
    channel.setDescription(descriptionText);
    return channel;
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private void createNotificationChannel(){
    NotificationChannel channel = new NotificationChannel("download_notification_channel",
            "Download notifications", NotificationManager.IMPORTANCE_NONE);
    NotificationManager service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    service.createNotificationChannel(channel);
  }

  @Override public void setupProgressNotification(String md5, String appName, int progress,
      boolean isIndeterminate) {

    NotificationCompat.Action downloadManagerAction = getDownloadManagerAction(md5.hashCode());
    PendingIntent appViewPendingIntent = getAppViewOpeningPendingIntent(md5);
    NotificationCompat.Action pauseAction = getPauseAction(md5);

    if (progressNotification == null) {
      progressNotification =
          buildNotification(appName, progress, isIndeterminate, pauseAction, downloadManagerAction,
              appViewPendingIntent);
    } else {
      long oldWhen = progressNotification.when;
      progressNotification =
          buildNotification(appName, progress, isIndeterminate, pauseAction, downloadManagerAction,
              appViewPendingIntent);
      progressNotification.when = oldWhen;
    }

    startForeground(NOTIFICATION_ID, progressNotification);
  }

  @Override public void removeProgressNotificationAndStop() {
    downloadsNotificationsPresenter.onDestroy();
    progressNotification = null;
    stopForeground(true);
    stopSelf();
  }

  private PendingIntent getAppViewOpeningPendingIntent(String md5) {
    Intent intent = createDeeplinkingIntent();

    final Bundle bundle = new Bundle();
    bundle.putBoolean(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    bundle.putString(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, md5);
    intent.putExtras(bundle);

    return PendingIntent.getActivity(this, OPEN_APPVIEW_REQUEST_CODE, intent,
        PendingIntent.FLAG_IMMUTABLE);
  }

  @NonNull private Intent createDeeplinkingIntent() {
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), AptoideApplication.getActivityProvider()
        .getMainActivityFragmentClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return intent;
  }
}
