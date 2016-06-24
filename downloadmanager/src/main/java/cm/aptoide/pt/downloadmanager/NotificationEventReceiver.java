package cm.aptoide.pt.downloadmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.logger.Logger;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by trinkes on 6/23/16.
 */
public class NotificationEventReceiver extends BroadcastReceiver {

	public static final String APP_ID_EXTRA = "APTOIDE_APPID_EXTRA";
	public static final String DOWNLOADMANAGER_ACTION_PAUSE = "cm.aptoide.downloadmanager.action.pause";
	public static final String DOWNLOADMANAGER_ACTION_OPEN = "cm.aptoide.downloadmanager.action.open";
	public static final String DOWNLOADMANAGER_ACTION_RESUME = "cm.aptoide.downloadmanager.action.resume";
	private static final String TAG = NotificationEventReceiver.class.getSimpleName();

	public void onReceive(Intent intent) {
		Logger.d(TAG, "onReceive() called with: " + "intent = [" + intent + "]");

		String action = intent.getAction();
		if (action != null) {
			switch (action) {
				case DOWNLOADMANAGER_ACTION_PAUSE:
					AptoideDownloadManager.getInstance().pauseAllDownloads();
					break;
				case DOWNLOADMANAGER_ACTION_OPEN:
					AptoideDownloadManager.getInstance().openAppsManager();
					break;
				case DOWNLOADMANAGER_ACTION_RESUME:
					if (intent.hasExtra(APP_ID_EXTRA)) {
						long appid = intent.getLongExtra(APP_ID_EXTRA, -1);
						if (appid > 0) {
							@Cleanup
							Realm realm = Database.get();
							try {
								AptoideDownloadManager.getInstance()
										.startDownload(AptoideDownloadManager.getInstance().getDownloadFromDb(realm, appid).clone());
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
						}
					}
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		onReceive(intent);
	}
}
