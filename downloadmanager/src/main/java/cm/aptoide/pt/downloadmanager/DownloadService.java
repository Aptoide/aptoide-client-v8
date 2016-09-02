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

import java.util.Locale;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import io.realm.Realm;
import lombok.Cleanup;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 5/18/16.
 */
public class DownloadService extends Service {

	public static final int NOTIFICATION_ID = 8;
	private static final String TAG = DownloadService.class.getSimpleName();
	CompositeSubscription subscriptions;
	private AptoideDownloadManager downloadManager;
	private Intent notificationClickIntent;
	private Intent pauseDownloadsIntent;
	private Intent openAppsManagerIntent;
	private Subscription notificationUpdateSubscription;
	private Notification notification;

	private void pauseDownloads(Intent intent) {
		// TODO: 7/4/16 trinkes pause with specific id
		long appId = intent.getLongExtra(AptoideDownloadManager.APP_ID_EXTRA, 0);
		if (appId > 0) {
			downloadManager.pauseDownload(appId);
		} else {
			downloadManager.pauseAllDownloads();
		}
	}

	private void startDownload(long appId) {
		if (appId > 0) {
			@Cleanup Realm realm = DeprecatedDatabase.get();
			Download download = downloadManager.getStoredDownload(appId, realm);
			if (download != null) {
				downloadManager.startDownload(download.clone())
						.first()
						.subscribe(download1 -> Logger.d(TAG, "startDownload" +
								"() " +
								"called with: " + "appId = [" + appId + "]"), Throwable::printStackTrace);
				setupNotifications();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		downloadManager = AptoideDownloadManager.getInstance();
		downloadManager.initDownloadService(this);
		subscriptions = new CompositeSubscription();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			if (action != null) {
				switch (action) {
					case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD:
						long appId = intent.getLongExtra(AptoideDownloadManager.APP_ID_EXTRA, 0);
						startDownload(appId);
						break;
					case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE:
						pauseDownloads(intent);
				}
			}
		} else {
			downloadManager.getCurrentDownload().first().subscribe(download -> {
				if (download != null) {
					startDownload(download.getAppId());
				}
			}, Throwable::printStackTrace);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		subscriptions.unsubscribe();
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void setupStopSelfMechanism() {
		subscriptions.add(downloadManager.getCurrentDownloads().filter(downloads -> downloads.size() <= 0).subscribe(downloads1 -> {
			stopSelf();
		}));
	}

	private void setupNotifications() {
		if (notificationUpdateSubscription == null || notificationUpdateSubscription.isUnsubscribed()) {
			openAppsManagerIntent = createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_OPEN, null);

			notificationUpdateSubscription = downloadManager.getCurrentDownload().subscribe(download -> {
				Bundle bundle = new Bundle();
				bundle.putLong(AptoideDownloadManager.APP_ID_EXTRA, download.getAppId());
				notificationClickIntent = createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_NOTIFICATION, bundle);

				bundle = new Bundle();
				bundle.putLong(AptoideDownloadManager.APP_ID_EXTRA, download.getAppId());

				PendingIntent pOpenAppsManager = getPendingIntent(openAppsManagerIntent, download);
				PendingIntent pNotificationClick = getPendingIntent(notificationClickIntent, download);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(AptoideDownloadManager.getContext());
				switch (download.getOverallDownloadStatus()) {
					case Download.PROGRESS:
						pauseDownloadsIntent = createNotificationIntent(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE, bundle);
						PendingIntent pPause = getPendingIntent(pauseDownloadsIntent, download);
						builder.addAction(R.drawable.ic_pause, AptoideDownloadManager.getContext().getString(R.string.pause_download), pPause);
						break;
				}

				if (notification == null) {
					notification = buildStandardNotification(download, pOpenAppsManager, pNotificationClick, builder).build();
				} else {
					long oldWhen = notification.when;
					notification = buildStandardNotification(download, pOpenAppsManager, pNotificationClick, builder).build();
					notification.when = oldWhen;
				}
				startForeground(NOTIFICATION_ID, notification);
				setupStopSelfMechanism();
			}, Throwable::printStackTrace);
			subscriptions.add(notificationUpdateSubscription);
		}
	}

	private NotificationCompat.Builder buildStandardNotification(Download download, PendingIntent pOpenAppsManager, PendingIntent pNotificationClick,
	                                                             NotificationCompat.Builder builder) {
		builder.setSmallIcon(AptoideDownloadManager.getInstance().getSettingsInterface().getMainIcon())
				.setContentTitle(String.format(Locale.ENGLISH, AptoideDownloadManager.getContext()
						.getResources()
						.getString(R.string.aptoide_downloading), Application.getConfiguration().getMarketName()))
				.setContentText(new StringBuilder().append(download.getAppName())
						.append(" - ")
						.append(download.getStatusName(AptoideDownloadManager.getContext())))
				.setContentIntent(pNotificationClick)
				.setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE, download.getOverallProgress(), false)
				.addAction(AptoideDownloadManager.getInstance().getSettingsInterface().getButton1Icon(), AptoideDownloadManager.getInstance()
						.getSettingsInterface()
						.getButton1Text(AptoideDownloadManager.getContext()), pOpenAppsManager);
		return builder;
	}

	private PendingIntent getPendingIntent(Intent intent, Download download) {
		return PendingIntent.getBroadcast(AptoideDownloadManager.getContext(), download.getFilesToDownload().get(0).getDownloadId(), intent, 0);
	}

	private Intent createNotificationIntent(String intentAction, @Nullable Bundle bundle) {
		Intent intent = new Intent(AptoideDownloadManager.getContext(), NotificationEventReceiver.class);
		intent.setAction(intentAction);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		return intent;
	}
}
