/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import java.util.Locale;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 5/18/16.
 */
@Deprecated public class DownloadService extends Service {

  public static final int NOTIFICATION_ID = 8;
  private static final String TAG = DownloadService.class.getSimpleName();
  CompositeSubscription subscriptions;
  private AptoideDownloadManager downloadManager;
  private Intent notificationClickIntent;
  private Intent pauseDownloadsIntent;
  private Intent openAppsManagerIntent;
  private Subscription notificationUpdateSubscription;
  private Notification notification;
  private Subscription stopMechanismSubscription;

  @Override public void onCreate() {
    super.onCreate();
    Logger.d(TAG, "Download service is starting");
    downloadManager = AptoideDownloadManager.getInstance();
    downloadManager.initDownloadService(this);
    subscriptions = new CompositeSubscription();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      String action = intent.getAction();
      if (action != null) {
        switch (action) {
          case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD:
            String md5 = intent.getStringExtra(AptoideDownloadManager.FILE_MD5_EXTRA);
            try {
              startDownload(md5);
            } catch (DownloadNotFoundException e) {
              CrashReport.getInstance().log(e);
            }
            break;
          case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE:
            pauseDownloads(intent);
        }
      }
    } else {
      downloadManager.getCurrentDownload().first().subscribe(download -> {
        if (download != null) {
          try {
            startDownload(download.getMd5());
          } catch (DownloadNotFoundException e) {
            CrashReport.getInstance().log(e);
          }
        }
      }, err -> {
        CrashReport.getInstance().log(err);
      });
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

  private void startDownload(String md5) throws DownloadNotFoundException {
    if (!TextUtils.isEmpty(md5)) {
      subscriptions.add(downloadManager.getDownload(md5).first().subscribe(download -> {
        downloadManager.startDownload(download)
            .first()
            .subscribe(downloadFromRealm -> Logger.d(TAG,
                "startDownload called with: md5 = [" + md5 + "]"), e -> {
              CrashReport.getInstance().log(e);
            });
        setupNotifications();
      }));
      return;
    }

    throw new DownloadNotFoundException("Unable to start a download without an md5");
  }

  private void pauseDownloads(Intent intent) {
    String md5 = intent.getStringExtra(AptoideDownloadManager.FILE_MD5_EXTRA);
    if (!TextUtils.isEmpty(md5)) {
      downloadManager.pauseDownload(md5);
    } else {
      downloadManager.pauseAllDownloads();
    }
  }

  private void setupNotifications() {
    if (notificationUpdateSubscription == null || notificationUpdateSubscription.isUnsubscribed()) {
      openAppsManagerIntent =
          createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_OPEN, null);

      downloadManager.getCurrentDownload().subscribe(download -> {
        Bundle bundle = new Bundle();
        bundle.putString(AptoideDownloadManager.FILE_MD5_EXTRA, download.getMd5());
        notificationClickIntent =
            createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_NOTIFICATION,
                bundle);

        bundle = new Bundle();
        bundle.putString(AptoideDownloadManager.FILE_MD5_EXTRA, download.getMd5());

        PendingIntent pOpenAppsManager = getPendingIntent(openAppsManagerIntent, download);
        PendingIntent pNotificationClick = getPendingIntent(notificationClickIntent, download);

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(AptoideDownloadManager.getContext());
        switch (download.getOverallDownloadStatus()) {
          case Download.PROGRESS:
            pauseDownloadsIntent =
                createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE,
                    bundle);
            PendingIntent pPause = getPendingIntent(pauseDownloadsIntent, download);
            builder.addAction(R.drawable.media_pause,
                AptoideDownloadManager.getContext().getString(R.string.pause_download), pPause);
            break;
        }

        if (notification == null) {
          notification = buildStandardNotification(download, pOpenAppsManager, pNotificationClick,
              builder).build();
        } else {
          long oldWhen = notification.when;
          notification = buildStandardNotification(download, pOpenAppsManager, pNotificationClick,
              builder).build();
          notification.when = oldWhen;
        }
        startForeground(NOTIFICATION_ID, notification);
        setupStopSelfMechanism();
      });

      notificationUpdateSubscription =
          downloadManager.getCurrentDownload().distinctUntilChanged().subscribe(download -> {
          }, e -> {
            CrashReport.getInstance().log(e);
          });
      subscriptions.add(notificationUpdateSubscription);
    }
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

  private PendingIntent getPendingIntent(Intent intent, Download download) {
    return PendingIntent.getBroadcast(AptoideDownloadManager.getContext(),
        download.getFilesToDownload().get(0).getDownloadId(), intent, 0);
  }

  private NotificationCompat.Builder buildStandardNotification(Download download,
      PendingIntent pOpenAppsManager, PendingIntent pNotificationClick,
      NotificationCompat.Builder builder) {
    builder.setSmallIcon(AptoideDownloadManager.getInstance().getSettingsInterface().getMainIcon())
        .setContentTitle(String.format(Locale.ENGLISH, AptoideDownloadManager.getContext()
                .getResources()
                .getString(R.string.aptoide_downloading),
            Application.getConfiguration().getMarketName()))
        .setContentText(new StringBuilder().append(download.getAppName())
            .append(" - ")
            .append(download.getStatusName(AptoideDownloadManager.getContext())))
        .setContentIntent(pNotificationClick)
        .setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE, download.getOverallProgress(),
            false)
        .addAction(AptoideDownloadManager.getInstance().getSettingsInterface().getButton1Icon(),
            AptoideDownloadManager.getInstance()
                .getSettingsInterface()
                .getButton1Text(AptoideDownloadManager.getContext()), pOpenAppsManager);
    return builder;
  }

  private void setupStopSelfMechanism() {
    if (stopMechanismSubscription == null || stopMechanismSubscription.isUnsubscribed()) {
      stopMechanismSubscription = downloadManager.getCurrentDownloads()
          .observeOn(Schedulers.computation())
          .filter(downloads -> downloads == null || downloads.size() <= 0)
          .subscribe(downloads -> {
            Logger.d(TAG, "Download service is stopping");
            stopSelf();
          }, err -> {
            CrashReport.getInstance().log(err);
          });
      subscriptions.add(stopMechanismSubscription);
    }
  }
}
