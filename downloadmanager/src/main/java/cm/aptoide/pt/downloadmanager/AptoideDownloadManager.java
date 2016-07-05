package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadNotificationActionsInterface;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import io.realm.RealmList;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import rx.Observable;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager {

	/***********
	 * Paths
	 *****************/
	public static final String EXTERNAL_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String DOWNLOADS_STORAGE_PATH = EXTERNAL_ABSOLUTE_PATH + "/.aptoide/";
	public static final String APK_PATH = DOWNLOADS_STORAGE_PATH + "apks/";
	public static final String OBB_PATH = EXTERNAL_ABSOLUTE_PATH + "/Android/obb/";
	public static final String GENERIC_PATH = DOWNLOADS_STORAGE_PATH + "generic/";
	private static final String TAG = AptoideDownloadManager.class.getSimpleName();
	private static AptoideDownloadManager instance;
	private static Context context;
	private Queue<Long> downloadQueue = new LinkedList<>();
	private HashMap<Long, DownloadTask> downloadTasks = new HashMap<>();
	private boolean isDownloading = false;
	@Getter(AccessLevel.MODULE) private DownloadNotificationActionsInterface downloadNotificationActionsInterface;
	@Getter(AccessLevel.MODULE) private DownloadSettingsInterface settingsInterface;

	public static Context getContext() {
		return context;
	}

	public static AptoideDownloadManager getInstance() {
		if (instance == null) {
			instance = new AptoideDownloadManager();
		}
		return instance;
	}

	public void init(Context context, ServiceConnection serviceConnection, DownloadNotificationActionsInterface
			downloadNotificationActionsInterface, DownloadSettingsInterface settingsInterface) {
		Intent intent = new Intent(context, DownloadService.class);
		if (!context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)) {
			throw new RuntimeException("Download service bound failed");
		}
		context.startService(intent);
		this.downloadNotificationActionsInterface = downloadNotificationActionsInterface;
		this.settingsInterface = settingsInterface;
	}

	void initDownloadService(Context context) {
		FileDownloader.init(context);
		AptoideDownloadManager.context = context;
		createDownloadDirs();
	}

	private void createDownloadDirs() {
		FileUtils.createDir(APK_PATH);
		FileUtils.createDir(OBB_PATH);
		FileUtils.createDir(GENERIC_PATH);
	}

	private void startDownloadTask(DownloadTask downloadTask) {
		if (isDownloading()) {
			if (!isDownloadOnStack(downloadTask.download.getAppId())) {
				downloadQueue.add(downloadTask.download.getAppId());
			}
		} else {
			downloadTask.startDownload();
		}
	}

	/**
	 * @param download info about the download to be made.
	 *
	 * @return Observable to be subscribed if download updates needed or null if download is done already
	 *
	 * @throws IllegalArgumentException if the appToDownload object is not filled correctly, this exception will be
	 *                                  thrown with the cause in the detail message.
	 * @see #startDownload(GetAppMeta.App)
	 */
	public Observable<Download> startDownload(Download download) throws IllegalArgumentException {
		if (getDownloadStatus(download.getAppId()).toBlocking().first() == Download.COMPLETED) {
			return Observable.fromCallable(() -> download);
		}

		DownloadTask downloadTask;
		if (downloadTasks.containsKey(download.getAppId())) {
			downloadTask = downloadTasks.get(download.getAppId());
		} else {
			downloadTask = new DownloadTask(download);
			downloadTasks.put(download.getAppId(), downloadTask);
		}

		startDownloadTask(downloadTask);
		return downloadTask.getObservable();
	}

	public void pauseDownload(long appId) {
		@Cleanup Realm realm = Database.get();
		Download download = getDownload(realm, appId);
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			FileDownloader.getImpl().pause(fileToDownload.getDownloadId());
		}
//		downloadQueue.add(appId);
	}

	/**
	 * Pause all the downloads
	 */
	public void pauseAllDownloads() {
		Logger.d(TAG, "pauseAllDownloads() called");

		FileDownloader.getImpl().pauseAll();
	}

	public Observable<Integer> getDownloadStatus(long appId) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = Database.get();
			Download downloadToCheck = getDownload(realm, appId);
			@Download.DownloadState int downloadStatus = Download.NOT_DOWNLOADED;
			if (downloadToCheck != null) {
				downloadStatus = downloadToCheck.getOverallDownloadStatus();
				if (downloadStatus == Download.COMPLETED) {
					downloadStatus = getStateIfFileExists(downloadToCheck);
				}
			}
			return downloadStatus;
		});
	}

	/**
	 * Observe changes to a download. This observable never completes it will emmit items whenever the download state changes.
	 *
	 * @param appId
	 *
	 * @return observable for download state changes.
	 */
	public Observable<Download> getDownload(long appId) {
		@Cleanup Realm realm = Database.get();
		final Download download = getDownload(realm, appId);
		if (download == null) {
			return Observable.error(new DownloadNotFoundException());
		} else {
			return download.<Download> asObservable().map(realmDownload -> download.clone());
		}
	}

	private Download getDownload(Realm realm, long appId) {
		return realm.where(Download.class).equalTo("appId", appId).findFirst();
	}

	@NonNull
	@Download.DownloadState
	int getStateIfFileExists(Download downloadToCheck) {
		@Download.DownloadState int downloadStatus = Download.COMPLETED;
		for (final FileToDownload fileToDownload : downloadToCheck.getFilesToDownload()) {
			if (!FileUtils.fileExists(fileToDownload.getFilePath())) {
				downloadStatus = Download.FILE_MISSING;
				break;
			}
		}
		return downloadStatus;
	}

	void currentDownloadFinished(long appId) {
		downloadTasks.remove(appId);
		startNextDownload();
	}

	void startNextDownload() {
		Logger.d(TAG, "startNextDownload() called with: ");
		DownloadTask nextDownload = downloadTasks.get(downloadQueue.poll());
		if (nextDownload != null) {
			nextDownload.startDownload();
		}
	}

	public void stopDownload(long appId) {
		Logger.d(TAG, "stopDownload() called with: " + "appId = [" + appId + "]");
		@Cleanup Realm realm = Database.get();
		Download download = getDownload(realm, appId);

		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			FileDownloader.getImpl().pause(fileToDownload.getDownloadId());
			FileUtils.removeFile(AptoideDownloadManager.DOWNLOADS_STORAGE_PATH + fileToDownload.getFileName());
		}
	}

	public void openAppsManager() {
		// TODO: 6/23/16 trinkes open apps manager
	}

	/**
	 * check if there is any download in progress
	 *
	 * @return true if there is at least 1 download in progress, false otherwise
	 */
	public boolean isDownloading() {
		return isDownloading;
	}

	public void setDownloading(boolean downloading) {
		isDownloading = downloading;
	}

	/**
	 * Check if the download is in queue already
	 *
	 * @param appId App id that identifies the download
	 *
	 * @return true if the download is on queue already, false otherwise
	 */
	public boolean isDownloadOnStack(long appId) {
//		boolean toReturn = false;
//		for (final DownloadTask downloadTask : downloadQueue) {
//			if (downloadTask.download.getAppId() == appId) {
//				toReturn = true;
//				break;
//			}
//		}
//		return toReturn;
		return downloadQueue.contains(appId);
	}
}
