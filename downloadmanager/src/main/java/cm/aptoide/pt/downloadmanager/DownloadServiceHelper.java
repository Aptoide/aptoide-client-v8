package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.preferences.Application;
import io.realm.Realm;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by trinkes on 7/4/16.
 */
public class DownloadServiceHelper {

	private final AptoideDownloadManager aptoideDownloadManager;

	public DownloadServiceHelper(AptoideDownloadManager aptoideDownloadManager) {
		this.aptoideDownloadManager = aptoideDownloadManager;
	}

	/**
	 * Pause all the running downloads
	 */
	public static void pauseAllDownloads() {
		Context context = Application.getContext();
		Intent intent = new Intent(context, DownloadService.class);
		intent.setAction(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
		context.startService(intent);
	}

	/**
	 * Pause a download
	 *
	 * @param appId appId of the download to stop
	 */
	public void pauseDownload(long appId) {
		startDownloadService(appId, AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
	}

	/**
	 * Starts a download. If there is a download running it is added to queue
	 *
	 * @param download Download to provide info to be able to make the download
	 *
	 * @return An observable that reports the download state
	 */
	public Observable<Download> startDownload(Download download) {
		return aptoideDownloadManager.getDownload(download.getAppId()).first().onErrorResumeNext(throwable -> {
			if (throwable instanceof DownloadNotFoundException) {
				return Observable.fromCallable(() -> {

					@Cleanup
					Realm realm = Database.get();
					Database.save(download.clone(), realm);
					return download;
				});
			}
			return Observable.error(throwable);
		}).concatWith(Observable.fromCallable(() -> {
			startDownloadService(download.getAppId(), AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
			return download;
		})).concatWith(aptoideDownloadManager.getDownload(download.getAppId()));
	}

	private void startDownloadService(long appId, String action) {
		Intent intent = new Intent(Application.getContext(), DownloadService.class);
		intent.putExtra(AptoideDownloadManager.APP_ID_EXTRA, appId);
		intent.setAction(action);
		Application.getContext().startService(intent);
	}

	/**
	 * Finds the download that is currently running
	 *
	 * @return an observable that reports the current download state
	 */
	public Observable<Download> getCurrentDownlaod() {
		return aptoideDownloadManager.getCurrentDownload();
	}

	/**
	 * Gets all the recorded downloads
	 *
	 * @return an observable with all downloads in database
	 */
	public Observable<List<Download>> getAllDownloads() {
		return aptoideDownloadManager.getDownloads();
	}

	/**
	 * This method finds all the downloads that are in {@link Download#IN_QUEUE} and {@link Download#PAUSED} states.
	 *
	 * @return an observable with a download list
	 */
	public Observable<List<Download>> getRunningDownloads() {
		return aptoideDownloadManager.getCurrentDownloads();
	}

	/**
	 * This method finds the download with the appId
	 *
	 * @param appId appId to the app
	 *
	 * @return an observable with the download
	 */
	public Observable<Download> getDownload(long appId) {
		return aptoideDownloadManager.getDownload(appId);
	}

	public void removeDownload(long appId) {
		aptoideDownloadManager.removeDownload(appId);
	}
}
