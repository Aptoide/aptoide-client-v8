package cm.aptoide.pt.downloadmanager.interfaces;

import android.support.annotation.DrawableRes;

/**
 * Created by trinkes on 6/28/16.
 */
public interface NotificationInterface {

	void button1Pressed();

	void notificationPressed(long appId);

	@DrawableRes
	int getMainIcon();
}
