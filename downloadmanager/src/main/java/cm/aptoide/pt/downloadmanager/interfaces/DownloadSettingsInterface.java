package cm.aptoide.pt.downloadmanager.interfaces;

import android.content.Context;
import android.support.annotation.DrawableRes;

/**
 * Created by trinkes on 6/28/16.
 */
public interface DownloadSettingsInterface {

	@DrawableRes
	int getMainIcon();

	String getButton1Text(Context context);
}
