package cm.aptoide.pt.downloadmanager.interfaces;

import android.content.Context;
import android.support.annotation.DrawableRes;

/**
 * Created by trinkes on 6/28/16.
 */
public interface DownloadSettingsInterface {

	@DrawableRes
	int getMainIcon();

	@DrawableRes
	int getButton1Icon();

	String getButton1Text(Context context);
}
