package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import io.realm.RealmList;
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
	@Getter private final static AptoideDownloadManager instance = new AptoideDownloadManager();
	private static final String TAG = AptoideDownloadManager.class.getSimpleName();

	private static Context context;

	public static Context getContext() {
		return context;
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
	 */
	public
	@Nullable
	Observable startDownload(GetAppMeta.App appToDownload) throws IllegalArgumentException {

		@Download.DownloadState int state = getDownloadStatus(appToDownload.getId()).toBlocking().first();
		if (state == Download.COMPLETED) {
			return Observable.fromCallable(() -> 100);
		}
		validateApp(appToDownload);

		Download download = new Download();
		download.setAppId(appToDownload.getId());

		download.setFilesToDownload(createDownloadsListFromApp(appToDownload));

		DownloadTask downloadTask = new DownloadTask(download);
		downloadTask.startDownload();

		return downloadTask.getObservable();
	}

	private RealmList<FileToDownload> createDownloadsListFromApp(GetAppMeta.App appToDownload) {
		RealmList<FileToDownload> downloads = new RealmList<>();
		downloads.add(FileToDownload.createFileToDownload(appToDownload.getFile()
				.getPath(), appToDownload.getId(), appToDownload.getFile().getMd5sum(), null, FileToDownload.APK));
		if (appToDownload.getObb() != null) {
			if (appToDownload.getObb().getMain() != null) {
				downloads.add(FileToDownload.createFileToDownload(appToDownload.getObb()
						.getMain()
						.getPath(), appToDownload.getId(), appToDownload.getObb()
						.getMain()
						.getMd5sum(), appToDownload.getObb()
						.getMain()
						.getFilename(), FileToDownload.OBB, appToDownload.getPackageName()));
			}

			if (appToDownload.getObb().getPatch() != null) {
				downloads.add(FileToDownload.createFileToDownload(appToDownload.getObb()
						.getPatch()
						.getPath(), appToDownload.getId(), appToDownload.getObb()
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
		}
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

	public void stopDownload(long appId) {
		Logger.d(TAG, "stopDownload() called with: " + "appId = [" + appId + "]");
		@Cleanup Realm realm = Database.get();
		Download download = getDownloadFromDb(realm, appId);

		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			FileDownloader.getImpl().pause(fileToDownload.getDownloadId());
			FileUtils.removeFile(AptoideDownloadManager.DOWNLOADS_STORAGE_PATH + fileToDownload.getFileName());
		}
	}
}
