package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.downloadmanager.interfaces.DownloadNotificationActionsInterface;
import cm.aptoide.pt.logger.Logger;

/**
 * Created by trinkes on 6/28/16.
 */
public class DownloadNotificationActionsActionsInterface implements DownloadNotificationActionsInterface {

	private static final String TAG = DownloadNotificationActionsActionsInterface.class.getSimpleName();

	@Override
	public void button1Pressed() {
		Logger.d(TAG, "button1Pressed() called with: " + "");
	}

	@Override
	public void notificationPressed(long appId) {
		Logger.d(TAG, "notificationPressed() called with: " + "appId = [" + appId + "]");
	}
}
