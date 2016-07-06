package cm.aptoide.pt.v8engine;

import android.content.Context;

import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;

/**
 * Created by trinkes on 6/28/16.
 */
public class DownloadManagerSettingsI implements DownloadSettingsInterface {
	
	@Override
	public int getMainIcon() {
		return V8Engine.getConfiguration().getIcon();
	}

	@Override
	public int getButton1Icon() {
		return R.drawable.ic_manager;
	}

	@Override
	public String getButton1Text(Context context) {
		return context.getString(R.string.open_apps_manager);
	}
}
