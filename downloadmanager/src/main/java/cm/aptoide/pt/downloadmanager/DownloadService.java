package cm.aptoide.pt.downloadmanager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cm.aptoide.pt.database.realm.Download;
import rx.Observable;

/**
 * Created by trinkes on 5/18/16.
 */
public class DownloadService extends Service {

	IBinder binder = new LocalBinder();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AptoideDownloadManager.getInstance().init(this);
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public Observable startDownload(String url, int appId) {
		return AptoideDownloadManager.getInstance().startDownload(new Download());
	}

	public class LocalBinder extends Binder {

		DownloadService getService() {
			// Return this instance of LocalService so clients can call public methods
			return DownloadService.this;
		}
	}
}
