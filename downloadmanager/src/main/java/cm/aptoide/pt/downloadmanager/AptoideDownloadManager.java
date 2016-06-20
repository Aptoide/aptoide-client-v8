package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.util.ArrayList;
import java.util.HashMap;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.RealmInteger;
import cm.aptoide.pt.database.realm.RealmString;
import cm.aptoide.pt.downloadmanager.model.DownloadState;
import cm.aptoide.pt.downloadmanager.model.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import lombok.Cleanup;
import lombok.Getter;
import rx.Observable;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager {

	@Getter private final static AptoideDownloadManager instance = new AptoideDownloadManager();
	private static HashMap<Long, DownloadTask> currentDownloads = new HashMap<>();
	private static Context context;

	public static Context getContext() {
		return context;
	}

	public void init(Context context) {
		FileDownloader.init(context);
		AptoideDownloadManager.context = context;
	}

	/**
	 * @param appToDownload info about the download to be made, there are 2 mandatory arguments:<p> {@link
	 *                      GetAppMeta.App#id}: which will identify the download</p <p> {@link
	 *                      cm.aptoide.pt.model.v7.listapp.File#path} or
	 *                      {@link cm.aptoide.pt.model.v7.listapp.File#path}:
	 *                      which will give the link for the download </p>
	 *
	 * @return Observable to be subscribed if download updates needed
	 *
	 * @throws IllegalArgumentException if the appToDownload object is not filled correctly, this exception will be
	 *                                  thrown with the cause in the detail message.
	 */
	public Observable startDownload(GetAppMeta.App appToDownload) throws IllegalArgumentException {

		validateApp(appToDownload);

		GetAppMeta.GetAppMetaFile file = appToDownload.getFile();
		ArrayList<FileToDownload> downloads = new ArrayList<>();

		downloads.add(new FileToDownload().setLink(file.getPath())
				.setAppId(appToDownload.getId())
				.setMd5(file.getMd5sum())
				.setFileType(FileToDownload.FileType.APK));

//		downloads.add(new FileToDownload().setLink(file.getPathAlt())
//				.setAppId(appToDownload.getId())
//				.setMd5(file.getMd5sum())
//				.setFileType(FileToDownload.FileType.APK));
		if (appToDownload.getObb() != null) {
			if (appToDownload.getObb().getMain() != null) {

				downloads.add(new FileToDownload().setLink(appToDownload.getObb().getMain().getPath())
						.setAppId(appToDownload.getId())
						.setMd5(file.getMd5sum())
						.setFileType(FileToDownload.FileType.OBB));
			}

			if (appToDownload.getObb().getMain() != null) {
				downloads.add(new FileToDownload().setLink(appToDownload.getObb().getPatch().getPath())
						.setAppId(appToDownload.getId())
						.setMd5(file.getMd5sum())
						.setFileType(FileToDownload.FileType.OBB));
			}
		}

//		downloads.add(new FileToDownload().setLink(url).setMd5("teste1"));
//		downloads.add(new FileToDownload().setLink("http://cdn4.aptoide" + "" +
//				".com/imgs/d/6/3/d637cde3051bdd981eadf0516797f7c5_icon.png").setMd5("teste2"));
//		downloads.add(new FileToDownload().setLink("http://cdn4.aptoide" + "" +
//				".com/imgs/2/b/3/2b343cc60cd92ee2548828114ac0f9d3_icon.png").setMd5("teste3"));
//		downloads.add(new FileToDownload().setLink("http://8ace.apk.aptoide" +
//				".com/glispastore/com-fshareapps-android-10001226-18925085" +
//				"-c0280b5144420856c21d861339514791.apk").setMd5("teste4"));
//		downloads.add(new FileToDownload().setLink("http://8ace.apk.aptoide" + "" +
//				".com/glispastore/com-fshareapps-android-10001226-18925085" +
//				"-c0280b5144420856c21d861339514791.apk").setMd5("teste5"));
//		downloads.add(new FileToDownload().setLink("http://8ace.apk.aptoide" + "" +
//				".com/glispastore/com-fshareapps-android-10001226-18925085" +
//				"-c0280b5144420856c21d861339514791.apk").setMd5("teste6"));

		DownloadTask downloadTask = new DownloadTask(appToDownload.getId(), downloads);
		currentDownloads.put(appToDownload.getId(), downloadTask);

//		Database db = new Database(context);
//
//		Download download = new Download();
//		download.setAppId(appId);
//		download.setString();
//
//		Updates updates = new Updates();
//		updates.setMd5("12kln31oh1o9j13m1pfn9hv");
//		db.copyOrUpdate(updates);
//
//		Updates updates2 =
//				db.getRealm()
//						.where(Updates.class)
//						.equalTo("appId", 1)
//						.findFirst();
//
//		db.runTransaction( ()-> updates2.setMd5("12kln31oh1o9j13m1pfn9hv") );

		return downloadTask.getObservable();
	}

	private void validateApp(GetAppMeta.App appToDownload) throws IllegalArgumentException {
		if (appToDownload.getId() <= 0) {
			throw new IllegalArgumentException("Invalid AppId");
		} else if (appToDownload.getFile() == null) {
			throw new IllegalArgumentException("The object GetAppMetaFile can't be null");
		} else if (TextUtils.isEmpty(appToDownload.getFile().getPath()) && TextUtils.isEmpty(appToDownload.getFile()
				.getPathAlt())) {
			throw new IllegalArgumentException("No download link provided");
		}
	}

	private DownloadState checkStateIfIsDownloaded(Download downloadToCheck) {
		DownloadState state = DownloadState.NOT_DOWNLOADED;
		try {
			if (downloadToCheck != null) {
				for (RealmInteger realmInteger : downloadToCheck.getDownloadId()) {
					int status = FileDownloader.getImpl().getStatus(realmInteger.getInteger());
					if (!(status == FileDownloadStatus.completed)) {
						return DownloadState.getEnumState(status);
					}
				}
				state = DownloadState.COMPLETED;
			}
		} catch (NullPointerException e) {
			Logger.printException(e);
			state = DownloadState.NOT_DOWNLOADED;
		}
		return state;
	}

	public Observable<DownloadState> getDownloadStatus(int appId) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = Database.get();
			Download downloadToCheck = getDownloadFromDb(realm, appId);
			DownloadState downloadStatus = checkStateIfIsDownloaded(downloadToCheck);
			if (downloadStatus.equals(DownloadState.COMPLETED)) {
				downloadStatus = getStateIfFileExists(downloadToCheck);
			}
			return downloadStatus;
		});
	}

	Download getDownloadFromDb(Realm realm, long appId) {
		return realm.where(Download.class).equalTo("appId", appId).findFirst();
	}

	@NonNull
	DownloadState getStateIfFileExists(Download downloadToCheck) {
		DownloadState downloadStatus = DownloadState.COMPLETED;
		if (downloadToCheck == null || downloadToCheck.getFilePaths().size() <= 0) {
			downloadStatus = DownloadState.FILE_MISSING;
		} else {
			for (RealmString path : downloadToCheck.getFilePaths()) {
				if (!FileUtils.fileExists(path.getString())) {
					downloadStatus = DownloadState.FILE_MISSING;
					break;
				}
			}
		}
		return downloadStatus;
	}

//	class DownloadListener extends FileDownloadListener{
//
//		@Override
//		protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//			Logger.d(TAG, "pending() called with: " + "task = [" + task + "], soFarBytes = [" +
//					soFarBytes + "], totalBytes = [" + totalBytes + "]");
//			getDownloadTask(task).setStatus(DownloadState.PENDING);
//		}
//
//		@Override
//		protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//			Logger.d(TAG, "progress() called with: " + "task = [" + task + "], soFarBytes = [" +
//					soFarBytes + "], totalBytes = [" + totalBytes + "]");
//			DownloadTask tag = getDownloadTask(task);
//			tag.setStatus(DownloadState.STARTED);
//			tag.progress(task, soFarBytes, totalBytes);
//		}
//
//		@Override
//		protected void completed(BaseDownloadTask task) {
//			DownloadTask downloadTask = getDownloadTask(task);
//			downloadTask.completed(task);
//			Logger.d(TAG, "completed() called with: " + "task = [" + task + "]" + downloadTask
//					.getFilePath() + "taskPath: " + task
//					.getPath());
//		}
//
//		@Override
//		protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//			Logger.d(TAG, "paused() called with: " + "task = [" + task + "], soFarBytes = [" +
//					soFarBytes + "], totalBytes = [" + totalBytes + "]");
//			getDownloadTask(task).setStatus(DownloadState.PAUSED);
//		}
//
//		@Override
//		protected void error(BaseDownloadTask task, Throwable e) {
//			Logger.d(TAG, "error() called with: " + "task = [" + task + "], e = [" + e + "]");
//			getDownloadTask(task).setStatus(DownloadState.ERROR);
//		}
//
//		@Override
//		protected void warn(BaseDownloadTask task) {
//			Logger.d(TAG, "warn() called with: " + "task = [" + task + "]");
//		}
//
//		private DownloadTask getDownloadTask(BaseDownloadTask task) {
//			return (DownloadTask) task.getTag(DownloadTask.APTOIDE_DOWNLOAD_TASK_TAG_KEY);
//		}
//	}
}
