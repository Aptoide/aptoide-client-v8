/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadEvent;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallService extends Service {

  public static final String TAG = "InstallService";

  public static final String ACTION_OPEN_DOWNLOAD_MANAGER = "OPEN_DOWNLOAD_MANAGER";
  public static final String ACTION_OPEN_APP_VIEW = "OPEN_APP_VIEW";
  public static final String ACTION_STOP_INSTALL = "STOP_INSTALL";
  public static final String ACTION_STOP_ALL_INSTALLS = "STOP_ALL_INSTALLS";
  public static final String ACTION_START_INSTALL = "START_INSTALL";
  public static final String ACTION_INSTALL_FINISHED = "INSTALL_FINISHED";
  public static final String EXTRA_INSTALLATION_MD5 = "INSTALLATION_MD5";
  public static final String EXTRA_INSTALLER_TYPE = "INSTALLER_TYPE";
  public static final int INSTALLER_TYPE_DEFAULT = 0;
  public static final int INSTALLER_TYPE_ROLLBACK = 1;

  private static final int NOTIFICATION_ID = 8;

  private AptoideDownloadManager downloadManager;

  private CompositeSubscription subscriptions;
  private Notification notification;
  private Installer rollbackInstaller;
  private Installer defaultInstaller;
  private InstallManager installManager;
  private Map<String, Integer> installerTypeMap;
  private Analytics analytics;

  @Override public void onCreate() {
    super.onCreate();
    Logger.d(TAG, "Install service is starting");
    downloadManager = ((V8Engine) getApplicationContext()).getDownloadManager();
    defaultInstaller = new InstallerFactory().create(this, InstallerFactory.DEFAULT);
    rollbackInstaller = new InstallerFactory().create(this, InstallerFactory.ROLLBACK);
    installManager =
        ((V8Engine) getApplicationContext()).getInstallManager(InstallerFactory.ROLLBACK);
    subscriptions = new CompositeSubscription();
    setupNotification();
    installerTypeMap = new HashMap();
    analytics = Analytics.getInstance();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      String md5 = intent.getStringExtra(EXTRA_INSTALLATION_MD5);
      if (ACTION_START_INSTALL.equals(intent.getAction())) {
        installerTypeMap.put(md5,
            intent.getIntExtra(EXTRA_INSTALLER_TYPE, INSTALLER_TYPE_ROLLBACK));
        subscriptions.add(downloadAndInstall(this, md5).subscribe(hasNext -> treatNext(hasNext),
            throwable -> removeNotificationAndStop()));
      } else if (ACTION_STOP_INSTALL.equals(intent.getAction())) {
        subscriptions.add(stopDownload(md5).subscribe(hasNext -> treatNext(hasNext),
            throwable -> removeNotificationAndStop()));
      } else if (ACTION_OPEN_APP_VIEW.equals(intent.getAction())) {
        openAppView(md5);
      } else if (ACTION_OPEN_DOWNLOAD_MANAGER.equals(intent.getAction())) {
        openDownloadManager();
      } else if (ACTION_STOP_ALL_INSTALLS.equals(intent.getAction())) {
        stopAllDownloads();
      }
    } else {
      subscriptions.add(
          downloadAndInstallCurrentDownload(this).subscribe(hasNext -> treatNext(hasNext),
              throwable -> removeNotificationAndStop()));
    }
    return START_STICKY;
  }

  @Override public void onDestroy() {
    subscriptions.unsubscribe();
    super.onDestroy();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private Observable<Boolean> stopDownload(String md5) {
    return downloadManager.pauseDownloadSync(md5).andThen(hasNextDownload());
  }

  private void stopAllDownloads() {
    downloadManager.pauseAllDownloads();
    removeNotificationAndStop();
  }

  private void treatNext(boolean hasNext) {
    if (!hasNext) {
      removeNotificationAndStop();
    }
  }

  private Observable<Boolean> downloadAndInstallCurrentDownload(Context context) {
    return downloadManager.getCurrentDownload()
        .first()
        .flatMap(currentDownload -> downloadAndInstall(context, currentDownload.getMd5()));
  }

  private Observable<Boolean> downloadAndInstall(Context context, String md5) {
    return downloadManager.getDownload(md5)
        .first()
        .flatMap(download -> downloadManager.startDownload(download))
        .doOnNext(download -> {
          stopOnDownloadError(download.getOverallDownloadStatus());
          if (download.getOverallDownloadStatus() == Download.PROGRESS) {
            DownloadEvent report =
                (DownloadEvent) analytics.get(download.getPackageName() + download.getVersionCode(),
                    DownloadEvent.class);
            if (report != null) {
              report.setDownloadHadProgress(true);
            }
          }
        })
        .first(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .doOnNext(download -> {
          DownloadEvent report =
              (DownloadEvent) analytics.get(download.getPackageName() + download.getVersionCode(),
                  DownloadEvent.class);
          if (report != null) {
            report.setResultStatus(DownloadInstallAnalyticsBaseBody.ResultStatus.SUCC);
            analytics.sendEvent(report);
          }
        })
        .flatMap(download -> stopForegroundAndInstall(context, download, true).doOnNext(
            success -> sendBackgroundInstallFinishedBroadcast(download)))
        .flatMap(completed -> hasNextDownload());
  }

  private void stopOnDownloadError(int downloadStatus) {
    if (downloadStatus == Download.ERROR) {
      removeNotificationAndStop();
    }
  }

  private Observable<Boolean> hasNextDownload() {
    return downloadManager.getCurrentDownloads()
        .first()
        .map(downloads -> downloads != null && !downloads.isEmpty());
  }

  private void removeNotificationAndStop() {
    stopForeground(true);
    stopSelf();
  }

  private void sendBackgroundInstallFinishedBroadcast(Download download) {
    sendBroadcast(
        new Intent(ACTION_INSTALL_FINISHED).putExtra(EXTRA_INSTALLATION_MD5, download.getMd5()));
    if (download.isScheduled()) {
      removeFromScheduled(download.getMd5());
    }
  }

  private void removeFromScheduled(String md5) {
    ScheduledAccessor scheduledAccessor = AccessorFactory.getAccessorFor(Scheduled.class);
    scheduledAccessor.delete(md5);
    Logger.d(TAG, "Removing schedulled download with appId " + md5);
  }

  private Observable<Void> stopForegroundAndInstall(Context context, Download download,
      boolean removeNotification) {
    Installer installer = getInstaller(download.getMd5());
    stopForeground(removeNotification);
    switch (download.getAction()) {
      case Download.ACTION_INSTALL:
        return installer.install(context, download.getMd5());
      case Download.ACTION_UPDATE:
        return installer.update(context, download.getMd5());
      case Download.ACTION_DOWNGRADE:
        return installer.downgrade(context, download.getMd5());
      default:
        return Observable.error(
            new IllegalArgumentException("Invalid download action " + download.getAction()));
    }
  }

  private Installer getInstaller(String md5) {
    Integer installerType = installerTypeMap.get(md5);
    Installer installer;
    switch (installerType) {
      case INSTALLER_TYPE_DEFAULT:
        installer = defaultInstaller;
        break;
      case INSTALLER_TYPE_ROLLBACK:
      default:
        installer = rollbackInstaller;
        break;
    }
    return installer;
  }

  private void setupNotification() {
    subscriptions.add(installManager.getCurrentInstallation().subscribe(progress -> {
      if (!progress.isIndeterminate()) {

        int requestCode = progress.getRequest().getFilesToDownload().get(0).getDownloadId();
        String md5 = progress.getRequest().getMd5();

        NotificationCompat.Action downloadManagerAction =
            getDownloadManagerAction(requestCode, md5);
        PendingIntent appViewPendingIntent =
            getPendingIntent(requestCode, ACTION_OPEN_APP_VIEW, md5);
        NotificationCompat.Action pauseAction = getPauseAction(requestCode, md5);

        if (notification == null) {
          notification =
              buildNotification(progress, pauseAction, downloadManagerAction, appViewPendingIntent);
        } else {
          long oldWhen = notification.when;
          notification =
              buildNotification(progress, pauseAction, downloadManagerAction, appViewPendingIntent);
          notification.when = oldWhen;
        }

        startForeground(NOTIFICATION_ID, notification);
      }
    }, throwable -> removeNotificationAndStop()));
  }

  @NonNull private NotificationCompat.Action getPauseAction(int requestCode, String md5) {
    Bundle appIdExtras = new Bundle();
    appIdExtras.putString(AptoideDownloadManager.FILE_MD5_EXTRA, md5);
    return getAction(cm.aptoide.pt.downloadmanager.R.drawable.media_pause,
        getString(cm.aptoide.pt.downloadmanager.R.string.pause_download), requestCode,
        ACTION_STOP_INSTALL, md5);
  }

  @NonNull private NotificationCompat.Action getDownloadManagerAction(int requestCode, String md5) {
    Bundle appIdExtras = new Bundle();
    appIdExtras.putString(AptoideDownloadManager.FILE_MD5_EXTRA, md5);
    return getAction(R.drawable.ic_manager, getString(R.string.open_apps_manager), requestCode,
        ACTION_OPEN_DOWNLOAD_MANAGER, md5);
  }

  private Notification buildNotification(Progress<Download> progress,
      NotificationCompat.Action pauseAction, NotificationCompat.Action openDownloadManager,
      PendingIntent contentIntent) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle(String.format(Locale.ENGLISH,
            getResources().getString(cm.aptoide.pt.downloadmanager.R.string.aptoide_downloading),
            Application.getConfiguration().getMarketName()))
        .setContentText(new StringBuilder().append(progress.getRequest().getAppName())
            .append(" - ")
            .append(progress.getRequest().getStatusName(this)))
        .setContentIntent(contentIntent)
        .setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE,
            progress.getRequest().getOverallProgress(), progress.isIndeterminate())
        .addAction(pauseAction)
        .addAction(openDownloadManager);
    return builder.build();
  }

  private NotificationCompat.Action getAction(int icon, String title, int requestCode,
      String action, String md5) {
    return new NotificationCompat.Action(icon, title, getPendingIntent(requestCode, action, md5));
  }

  private PendingIntent getPendingIntent(int requestCode, String action, String md5) {
    Intent intent = new Intent(this, InstallService.class);
    if (!TextUtils.isEmpty(md5)) {
      final Bundle bundle = new Bundle();
      bundle.putString(EXTRA_INSTALLATION_MD5, md5);
      intent.putExtras(bundle);
    }
    return PendingIntent.getService(this, requestCode, intent.setAction(action),
        PendingIntent.FLAG_ONE_SHOT);
  }

  private void openDownloadManager() {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION, true);
    startActivity(intent);
  }

  private void openAppView(String md5) {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, md5);
    startActivity(intent);
  }

  @NonNull private Intent createDeeplinkingIntent() {
    Intent intent = new Intent();
    intent.setClass(Application.getContext(),
        V8Engine.getActivityProvider().getMainActivityFragmentClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return intent;
  }
}
