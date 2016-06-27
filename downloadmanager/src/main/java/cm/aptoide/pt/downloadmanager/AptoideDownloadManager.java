package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import io.realm.RealmList;
import lombok.Cleanup;
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

	public static Context getContext() {
		return context;
	}

	public static AptoideDownloadManager getInstance() {
		if (instance == null) {
			instance = new AptoideDownloadManager();
		}
		return instance;
	}

	public void init(Context context) {
		FileDownloader.init(context);
		AptoideDownloadManager.context = context;
		createDownloadDirs();
	}

	private void createDownloadDirs() {
		FileUtils.createDir(APK_PATH);
		FileUtils.createDir(OBB_PATH);
		FileUtils.createDir(GENERIC_PATH);
	}

	/**
	 * @param appToDownload info about the download to be made, there are 2 mandatory arguments:<p> {@link
	 *                      GetAppMeta.App#id}: which will identify the download</p <p> {@link
	 *                      cm.aptoide.pt.model.v7.listapp.File#path} or
	 *                      {@link cm.aptoide.pt.model.v7.listapp.File#path}:
	 *                      which will give the link for the download </p>
	 *
	 * @return Observable to be subscribed if download updates needed or null if download is done already
	 *
	 * @throws IllegalArgumentException if the appToDownload object is not filled correctly, this exception will be
	 *                                  thrown with the cause in the detail message.
	 * @see #startDownload(Download)
	 */
	public Observable<Integer> startDownload(GetAppMeta.App appToDownload) throws IllegalArgumentException {
		if (getDownloadStatus(appToDownload.getId()).toBlocking().first() == Download.COMPLETED) {
			return Observable.fromCallable(() -> 100);
		}
		validateApp(appToDownload);

		DownloadTask downloadTask;
		if (downloadTasks.containsKey(appToDownload.getId())) {
			downloadTask = downloadTasks.get(appToDownload.getId());
		} else {
			Download download = new Download();
			download.setAppId(appToDownload.getId());
			download.setAppName(appToDownload.getName());
			download.setFilesToDownload(createDownloadsListFromApp(appToDownload));
			downloadTask = new DownloadTask(download);
			downloadTasks.put(appToDownload.getId(), downloadTask);
		}
		startDownloadTask(downloadTask);
		return downloadTask.getObservable();
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
	public Observable<Integer> startDownload(Download download) throws IllegalArgumentException {
		if (getDownloadStatus(download.getAppId()).toBlocking().first() == Download.COMPLETED) {
			return Observable.fromCallable(() -> 100);
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

	private RealmList<FileToDownload> createDownloadsListFromApp(GetAppMeta.App appToDownload) {
		RealmList<FileToDownload> downloads = new RealmList<>();
		downloads.add(FileToDownload.createFileToDownload(appToDownload.getFile()
				.getPath(), appToDownload.getId(), appToDownload.getFile().getMd5sum(), null, FileToDownload.APK));
		if (appToDownload.getObb() != null) {
			if (appToDownload.getObb().getMain() != null) {
				downloads.add(FileToDownload.createFileToDownload(appToDownload.getObb()
						.getMain().getPath(), appToDownload.getId(), appToDownload.getObb()
						.getMain()
						.getMd5sum(), appToDownload.getObb()
						.getMain()
						.getFilename(), FileToDownload.OBB, appToDownload.getPackageName()));
			}

			if (appToDownload.getObb().getPatch() != null) {
				downloads.add(FileToDownload.createFileToDownload(appToDownload.getObb()
						.getPatch().getPath(), appToDownload.getId(), appToDownload.getObb()
						.getPatch()
						.getMd5sum(), appToDownload.getObb().getPatch().getFilename(), FileToDownload.OBB,
						appToDownload
						.getPackageName()));
			}
		}

		return downloads;
	}

	private void validateApp(GetAppMeta.App appToDownload) throws IllegalArgumentException {
		if (appToDownload.getId() <= 0) {
			throw new IllegalArgumentException("Invalid AppId");
		} else if (appToDownload.getFile() == null) {
			throw new IllegalArgumentException("The object GetAppMetaFile can't be null");
		} else if (TextUtils.isEmpty(appToDownload.getFile().getPath()) && TextUtils.isEmpty(appToDownload.getFile()
				.getPathAlt())) {
			throw new IllegalArgumentException("No download link provided");
		} else if (appToDownload.getObb() != null && TextUtils.isEmpty(appToDownload.getPackageName())) {
			throw new IllegalArgumentException("This app has an OBB and doesn't have the package name specified");
		} else if (TextUtils.isEmpty(appToDownload.getName())) {
			throw new IllegalArgumentException("This app has an OBB and doesn't have the App name specified");
		}
	}

	public void pauseDownload(long appId) {
		@Cleanup Realm realm = Database.get();
		Download download = getDownloadFromDb(realm, appId);
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
			Download downloadToCheck = getDownloadFromDb(realm, appId);
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

	// TODO: 6/22/16 trinkes add method to access this one
	public Download getDownloadFromDb(Realm realm, long appId) {
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
		Download download = getDownloadFromDb(realm, appId);

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
