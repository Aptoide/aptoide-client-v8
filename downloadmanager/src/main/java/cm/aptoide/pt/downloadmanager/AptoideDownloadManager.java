/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.interfaces.CacheManager;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadNotificationActionsInterface;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import com.liulishuo.filedownloader.FileDownloader;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager {

	public static final String APP_ID_EXTRA = "APTOIDE_APPID_EXTRA";
	public static final String DOWNLOADMANAGER_ACTION_PAUSE =
			"cm.aptoide.downloadmanager.action.pause";
	public static final String DOWNLOADMANAGER_ACTION_OPEN = "cm.aptoide.downloadmanager.action.open";
	public static final String DOWNLOADMANAGER_ACTION_START_DOWNLOAD =
			"cm.aptoide.downloadmanager.action.start.download";
	public static final String DOWNLOADMANAGER_ACTION_NOTIFICATION =
			"cm.aptoide.downloadmanager.action.notification";
	static public final int PROGRESS_MAX_VALUE = 100;
	private static final String TAG = AptoideDownloadManager.class.getSimpleName();
	private static final int VALUE_TO_CONVERT_MB_TO_BYTES = 1024 * 1024;
	/***********
	 * Paths
	 *****************/
	static String DOWNLOADS_STORAGE_PATH;
	static String APK_PATH;
	static String OBB_PATH;
	static String GENERIC_PATH;
	private static AptoideDownloadManager instance;
	private static Context context;
	private boolean isDownloading = false;
	private boolean isPausing = false;
	@Getter(AccessLevel.MODULE) private DownloadNotificationActionsInterface
			downloadNotificationActionsInterface;
	@Getter(AccessLevel.MODULE) private DownloadSettingsInterface settingsInterface;
	private DownloadAccessor downloadAccessor;
	private CacheManager cacheHelper;

	public static Context getContext() {
		return context;
	}

	public static AptoideDownloadManager getInstance() {
		if (instance == null) {
			instance = new AptoideDownloadManager();
		}
		return instance;
	}

	void initDownloadService(Context context) {
		AptoideDownloadManager.context = context;
		createDownloadDirs();
	}

	private void createDownloadDirs() {
		FileUtils.createDir(APK_PATH);
		FileUtils.createDir(OBB_PATH);
		FileUtils.createDir(GENERIC_PATH);
	}

	/**
	 * @param download info about the download to be made.
	 * @return Observable to be subscribed if download updates needed or null if download is done
	 * already
	 * @throws IllegalArgumentException if the appToDownload object is not filled correctly, this
	 * exception will be thrown with the cause in the detail
	 * message.
	 */
	public Observable<Download> startDownload(Download download) throws IllegalArgumentException {
		return getDownloadStatus(download.getAppId()).flatMap(status -> {
			if (status == Download.COMPLETED) {
				return Observable.just(download);
			} else {
				Observable.fromCallable(() -> {
					startNewDownload(download);
					return null;
				}).subscribeOn(Schedulers.computation()).subscribe(o -> {
				}, Throwable::printStackTrace);
				return getDownload(download.getAppId());
			}
		});
	}

	private void startNewDownload(Download download) {
		download.setOverallDownloadStatus(Download.IN_QUEUE);
		//commented to prevent the ui glitch with "0" value
		// (trusting in progress value from outside can be dangerous)
		//		download.setOverallProgress(0);
		download.setTimeStamp(System.currentTimeMillis());
		downloadAccessor.save(download);

		startNextDownload();
	}

	public void pauseDownload(long appId) {
		downloadAccessor.get(appId).first().map(download -> {
			download.setOverallDownloadStatus(Download.PAUSED);
			downloadAccessor.save(download);
			for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
				FileDownloader.getImpl().pause(fileToDownload.getDownloadId());
			}
			return download;
		}).subscribe(download -> {
			Logger.d(TAG, "Download with " + appId + " paused");
		}, throwable -> {
			if (throwable instanceof DownloadNotFoundException) {
				Logger.d(TAG, "there are no download to pause with the id: " + appId);
			} else {
				throwable.printStackTrace();
			}
		});
	}

	/**
	 * Observe changes to a download. This observable never completes it will emmit items whenever
	 * the
	 * download state changes.
	 *
	 * @return observable for download state changes.
	 */
	public Observable<Download> getDownload(long appId) {
		return downloadAccessor.get(appId).flatMap(download -> {
			if (download == null || (download.getOverallDownloadStatus() == Download.COMPLETED
					&& getInstance().getStateIfFileExists(download) == Download.FILE_MISSING)) {
				return Observable.error(new DownloadNotFoundException());
			} else {
				return Observable.just(download);
			}
		}).takeUntil(storedDownload -> storedDownload.getOverallDownloadStatus() == Download.COMPLETED);
	}

	public Observable<Download> getCurrentDownload() {
		return getDownloads().flatMapIterable(downloads -> downloads)
				.filter(downloads -> downloads.getOverallDownloadStatus() == Download.PROGRESS);
	}

	public Observable<List<Download>> getCurrentDownloads() {
		return downloadAccessor.getRunningDownloads();
	}

	public Observable<List<Download>> getDownloads() {
		return downloadAccessor.getAll();
	}

	/**
	 * Pause all the downloads
	 */
	public void pauseAllDownloads() {
		FileDownloader.getImpl().pauseAll();
		isPausing = true;

		downloadAccessor.getRunningDownloads()
				.first()
				.doOnUnsubscribe(() -> isPausing = false)
				.subscribe(downloads -> {
					for (int i = 0; i < downloads.size(); i++) {
						downloads.get(i).setOverallDownloadStatus(Download.PAUSED);
					}
					downloadAccessor.save(downloads);
					Logger.d(TAG, "Downloads paused");
				}, Throwable::printStackTrace);
	}

	private Observable<Integer> getDownloadStatus(long appId) {
		return getDownload(appId).map(download -> {
			if (download != null) {
				if (download.getOverallDownloadStatus() == Download.COMPLETED) {
					return getStateIfFileExists(download);
				}
				return download.getOverallDownloadStatus();
			} else {
				return Download.NOT_DOWNLOADED;
			}
		});
	}

	public void init(Context context,
			DownloadNotificationActionsInterface downloadNotificationActionsInterface,
			DownloadSettingsInterface settingsInterface, DownloadAccessor downloadAccessor,
			CacheManager cacheHelper) {

		FileDownloader.init(context);
		this.downloadNotificationActionsInterface = downloadNotificationActionsInterface;
		this.settingsInterface = settingsInterface;
		this.cacheHelper = cacheHelper;

		DOWNLOADS_STORAGE_PATH = settingsInterface.getDownloadDir();
		APK_PATH = DOWNLOADS_STORAGE_PATH + "apks/";
		GENERIC_PATH = DOWNLOADS_STORAGE_PATH + "generic/";
		OBB_PATH = settingsInterface.getObbDir();
		if (TextUtils.isEmpty(OBB_PATH)) {
			OBB_PATH = GENERIC_PATH;
		}
		this.downloadAccessor = downloadAccessor;
	}

	@NonNull @Download.DownloadState private int getStateIfFileExists(Download downloadToCheck) {
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
		startNextDownload();
	}

	synchronized void startNextDownload() {
		if (!isDownloading && !isPausing) {
			isDownloading = true;
			getNextDownload().first().subscribe(download -> {
				if (download != null) {
					new DownloadTask(downloadAccessor, download).startDownload();
					Logger.d(TAG, "Download with id " + download.getAppId() + " started");
				} else {
					isDownloading = false;
					cacheHelper.cleanCache();
				}
			}, throwable -> throwable.printStackTrace());
		}
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

	public Observable<Download> getNextDownload() {
		return downloadAccessor.getInQueueSortedDownloads().map(downloads -> {
			if (downloads == null || downloads.size() <= 0) {
				return null;
			} else {
				return downloads.get(0);
			}
		});
	}

	public void removeDownload(long appId) {
		downloadAccessor.get(appId).map(download -> {
			for (int i = 0; i < download.getFilesToDownload().size(); i++) {
				final FileToDownload fileToDownload = download.getFilesToDownload().get(i);
				FileDownloader.getImpl()
						.clear(fileToDownload.getDownloadId(), fileToDownload.getFilePath());
			}
			return download;
		}).first(download -> download.getOverallDownloadStatus() != Download.PROGRESS).map(download -> {
			deleteDownloadFiles(download);
			deleteDownloadFromDb(download.getAppId());
			return download;
		}).subscribe(aVoid -> {
		}, throwable -> {
			if (throwable instanceof DownloadNotFoundException) {
				Logger.d(TAG, "Download not found, are you pressing on remove button too fast?");
			} else if (throwable instanceof NullPointerException) {
				Logger.d(TAG, "Download item was null, are you pressing on remove button too fast?");
			} else {
				throwable.printStackTrace();
			}
		});
	}

	private void deleteDownloadFromDb(long downloadId) {
		downloadAccessor.delete(downloadId);
	}

	private void deleteDownloadFiles(Download download) {
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (download.getOverallDownloadStatus() == Download.COMPLETED) {
				FileUtils.removeFile(fileToDownload.getFilePath());
			} else {
				FileUtils.removeFile(DOWNLOADS_STORAGE_PATH + fileToDownload.getFileName() + ".temp");
			}
		}
	}
}
