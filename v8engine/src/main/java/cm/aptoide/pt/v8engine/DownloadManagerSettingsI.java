package cm.aptoide.pt.v8engine;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.utils.AptoideUtils;

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

	@Override
	public long getMaxCacheSize() {
		return 81920;
	}

	@NonNull
	@Override
	public String getDownloadDir() {
		return AptoideUtils.SystemU.getDownloadFolderPath();
	}

	@Override
	public String getObbDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/";
	}
}
