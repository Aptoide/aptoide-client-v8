/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.NotificationEventReceiver;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.install.BackgroundInstaller;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.install.Progress;
import java.util.Locale;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallService extends Service {

  public static final String TAG = "InstallService";

  public static final String ACTION_START_INSTALL = "START_INSTALL";
  public static final String ACTION_INSTALL_FINISHED = "INSTALL_FINISHED";
  public static final String EXTRA_INSTALLATION_ID = "INSTALLATION_ID";

  private static final int NOTIFICATION_ID = 8;

  private AptoideDownloadManager downloadManager;

  private CompositeSubscription subscriptions;

  private Notification notification;
  private Installer installer;
  private BackgroundInstaller backgroundInstaller;

  @Override public void onCreate() {
    super.onCreate();
    Logger.d(TAG, "Install service is starting");
    downloadManager = AptoideDownloadManager.getInstance();
    downloadManager.initDownloadService(this);
    installer = new InstallerFactory().create(this, InstallerFactory.ROLLBACK);
    backgroundInstaller = new BackgroundInstaller(downloadManager, installer,
        AccessorFactory.getAccessorFor(Download.class));
    subscriptions = new CompositeSubscription();
    setupNotification();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      long installationId = intent.getLongExtra(EXTRA_INSTALLATION_ID, -1);
      if (ACTION_START_INSTALL.equals(intent.getAction())) {
        subscriptions.add(
            downloadAndInstall(this, installationId).subscribe(
                hasNext -> treatNext(hasNext), throwable -> removeNotificationAndStop(throwable)));
      }
    } else {
      subscriptions.add(
          downloadAndInstallCurrentDownload(this).subscribe(hasNext -> treatNext(hasNext),
              throwable -> removeNotificationAndStop(throwable)));
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

  private void treatNext(boolean hasNext) {
    if (!hasNext) {
      removeNotificationAndStop(new IllegalStateException("No pending downloads. Stop service."));
    }
  }

  private Observable<Boolean> downloadAndInstallCurrentDownload(Context context) {
    return downloadManager.getCurrentDownload()
        .first()
        .flatMap(currentDownload -> downloadAndInstall(context, currentDownload.getAppId()));
  }

  private Observable<Boolean> downloadAndInstall(Context context, long installId) {
    return downloadManager.getDownload(installId)
        .first()
        .flatMap(download -> downloadManager.startDownload(download))
        .first(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .flatMap(download -> stopForegroundAndInstall(context, download, true))
        .doOnNext(download -> sendBackgroundInstallFinishedBroadcast(installId))
        .flatMap(completed -> hasNextDownload());
  }

  private Observable<Boolean> hasNextDownload() {
    return downloadManager.getCurrentDownloads()
        .map(downloads -> downloads != null && !downloads.isEmpty());
  }

  private void removeNotificationAndStop(Throwable throwable) {
    throwable.printStackTrace();
    stopForeground(true);
    stopSelf();
  }

  private void sendBackgroundInstallFinishedBroadcast(long installId) {
    sendBroadcast(new Intent(ACTION_INSTALL_FINISHED).putExtra(EXTRA_INSTALLATION_ID, installId));
  }

  private Observable<Void> stopForegroundAndInstall(Context context, Download download,
      boolean removeNotification) {
    stopForeground(removeNotification);
    switch (download.getAction()) {
      case Download.ACTION_INSTALL:
        return installer.install(context, download.getAppId());
      case Download.ACTION_UPDATE:
        return installer.update(context, download.getAppId());
      case Download.ACTION_DOWNGRADE:
        return installer.downgrade(context, download.getAppId());
      default:
        return Observable.error(
            new IllegalArgumentException("Invalid download action " + download.getAction()));
    }
  }

  private void setupStopSelfMechanism() {
    subscriptions.add(downloadManager.getCurrentDownloads()
        .observeOn(Schedulers.computation())
        .filter(downloads -> downloads == null || downloads.size() <= 0)
        .subscribe(downloads -> stopSelf(), Throwable::printStackTrace));
  }

  private void setupNotification() {

    subscriptions.add(backgroundInstaller.getCurrentInstallation().subscribe(progress -> {
      if (!progress.isIndeterminate()) {
        Bundle bundle = new Bundle();
        bundle.putLong(AptoideDownloadManager.APP_ID_EXTRA, progress.getRequest().getAppId());

        bundle = new Bundle();
        bundle.putLong(AptoideDownloadManager.APP_ID_EXTRA, progress.getRequest().getAppId());

        PendingIntent pOpenAppsManager = getPendingIntent(
            createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_OPEN, null),
            progress.getRequest());
        PendingIntent pNotificationClick = getPendingIntent(
            createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_NOTIFICATION,
                bundle), progress.getRequest());
        PendingIntent pauseIntent = getPendingIntent(
            createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE, bundle),
            progress.getRequest());

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(AptoideDownloadManager.getContext());

        if (progress.getRequest().getOverallDownloadStatus() == Download.PAUSED) {

        } else {

        }

        if (notification == null) {
          notification = buildStandardNotification(progress, pOpenAppsManager, pNotificationClick,
              builder).build();
        } else {
          long oldWhen = notification.when;
          notification = buildStandardNotification(progress, pOpenAppsManager, pNotificationClick,
              builder).build();
          notification.when = oldWhen;
        }
        startForeground(NOTIFICATION_ID, notification);
      }
    }, throwable -> removeNotificationAndStop(throwable)));
  }

  private NotificationCompat.Builder buildStandardNotification(Progress<Download> progress,
      PendingIntent pOpenAppsManager, PendingIntent pNotificationClick,
      NotificationCompat.Builder builder) {
    builder.setSmallIcon(AptoideDownloadManager.getInstance().getSettingsInterface().getMainIcon())
        .setContentTitle(String.format(Locale.ENGLISH, AptoideDownloadManager.getContext()
                .getResources()
                .getString(cm.aptoide.pt.downloadmanager.R.string.aptoide_downloading),
            Application.getConfiguration().getMarketName()))
        .setContentText(new StringBuilder().append(progress.getRequest().getAppName())
            .append(" - ")
            .append(progress.getRequest().getStatusName(AptoideDownloadManager.getContext())))
        .setContentIntent(pNotificationClick)
        .setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE,
            progress.getRequest().getOverallProgress(), progress.isIndeterminate())
        .addAction(AptoideDownloadManager.getInstance().getSettingsInterface().getButton1Icon(),
            AptoideDownloadManager.getInstance()
                .getSettingsInterface()
                .getButton1Text(AptoideDownloadManager.getContext()), pOpenAppsManager);
    return builder;
  }

  private PendingIntent getPendingIntent(Intent intent, Download download) {
    return PendingIntent.getBroadcast(AptoideDownloadManager.getContext(),
        download.getFilesToDownload().get(0).getDownloadId(), intent, 0);
  }

  private Intent createNotificationIntent(String intentAction, @Nullable Bundle bundle) {
    Intent intent =
        new Intent(AptoideDownloadManager.getContext(), NotificationEventReceiver.class);
    intent.setAction(intentAction);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    return intent;
  }
}
