/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Download;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by trinkes on 6/23/16.
 */
public class NotificationEventReceiver extends BroadcastReceiver {

	private static final String TAG = NotificationEventReceiver.class.getSimpleName();

	public void onReceive(Intent intent) {

		String action = intent.getAction();
		if (action != null) {
			AptoideDownloadManager downloadManager = AptoideDownloadManager.getInstance();
			switch (action) {
				case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE:
					if (intent.hasExtra(AptoideDownloadManager.APP_ID_EXTRA)) {
						long appid = intent.getLongExtra(AptoideDownloadManager.APP_ID_EXTRA, -1);
						if (appid > 0) {
							downloadManager.pauseDownload(appid);
						} else {
							downloadManager.pauseAllDownloads();
						}
					}
					break;
				case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_OPEN:
					if (downloadManager.getDownloadNotificationActionsInterface() != null) {
						downloadManager.getDownloadNotificationActionsInterface()
								.button1Pressed();
					}
					break;
				case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD:
					if (intent.hasExtra(AptoideDownloadManager.APP_ID_EXTRA)) {
						long appid = intent.getLongExtra(AptoideDownloadManager.APP_ID_EXTRA, -1);
						if (appid > 0) {
							@Cleanup Realm realm = DeprecatedDatabase.get();
							Download download = downloadManager.getStoredDownload(appid, realm);
							if (download != null) {
								downloadManager.startDownload(download.clone());
							}
						}
					}
					break;
				case AptoideDownloadManager.DOWNLOADMANAGER_ACTION_NOTIFICATION:
					if (downloadManager.getDownloadNotificationActionsInterface() != null) {
						if (intent.hasExtra(AptoideDownloadManager.APP_ID_EXTRA)) {
							downloadManager.getDownloadNotificationActionsInterface()
									.notificationPressed(intent.getLongExtra(AptoideDownloadManager.APP_ID_EXTRA, 0));
						}
						break;
					}
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		onReceive(intent);
	}
}
