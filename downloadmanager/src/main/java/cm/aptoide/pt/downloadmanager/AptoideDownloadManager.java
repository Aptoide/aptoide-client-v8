package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.support.annotation.NonNull;

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
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import lombok.Getter;
import rx.Observable;

/**
 * Created by trinkes on 5/13/16.
 */
public class AptoideDownloadManager {

	@Getter private final static AptoideDownloadManager instance = new AptoideDownloadManager();
	private static HashMap<Integer, DownloadTask> currentDownloads = new HashMap<>();
	private static Context context;

	public static Context getContext() {
		return context;
	}

	public Database getDatabase() {
		return new Database(context).open();
	}

	public void init(Context context) {
		FileDownloader.init(context);
		AptoideDownloadManager.context = context;
	}

	public Observable startDownload(String url, int appId) {

		ArrayList<FileToDownload> downloads = new ArrayList<>();

		downloads.add(new FileToDownload().setLink(url).setMd5("teste1"));
		downloads.add(new FileToDownload().setLink("http://cdn4.aptoide" + "" +
				".com/imgs/d/6/3/d637cde3051bdd981eadf0516797f7c5_icon.png").setMd5("teste2"));
		downloads.add(new FileToDownload().setLink("http://cdn4.aptoide" + "" +
				".com/imgs/2/b/3/2b343cc60cd92ee2548828114ac0f9d3_icon.png").setMd5("teste3"));
		downloads.add(new FileToDownload().setLink("http://8ace.apk.aptoide" +
				".com/glispastore/com-fshareapps-android-10001226-18925085" +
				"-c0280b5144420856c21d861339514791.apk").setMd5("teste4"));
		downloads.add(new FileToDownload().setLink("http://8ace.apk.aptoide" + "" +
				".com/glispastore/com-fshareapps-android-10001226-18925085" +
				"-c0280b5144420856c21d861339514791.apk").setMd5("teste5"));
		downloads.add(new FileToDownload().setLink("http://8ace.apk.aptoide" + "" +
				".com/glispastore/com-fshareapps-android-10001226-18925085" +
				"-c0280b5144420856c21d861339514791.apk").setMd5("teste6"));

		DownloadTask downloadTask = new DownloadTask(appId, downloads);
		currentDownloads.put(appId, downloadTask);

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

	DownloadState checkStateIfIsDownloaded(Download downloadToCheck) {
		DownloadState state = DownloadState.INVALID_STATUS;
		if (downloadToCheck != null) {
			for (RealmInteger realmInteger : downloadToCheck.getDownloadId()) {
				int status = FileDownloader.getImpl().getStatus(realmInteger.getInteger());
				if (!(status == FileDownloadStatus.completed)) {
					return DownloadState.getEnumState(status);
				}
			}
			state = DownloadState.COMPLETED;
		}
		return state;
	}

	public Observable<DownloadState> getDownloadStatus(int appId) {
		return Observable.fromCallable(() -> {
			Database open = getDatabase();
			Download downloadToCheck = getDownloadFromDb(open, appId);
			open.close();
			DownloadState downloadStatus = DownloadState.INVALID_STATUS;
			downloadStatus = checkStateIfIsDownloaded(downloadToCheck);
			if (downloadStatus.equals(DownloadState.COMPLETED)) {
				downloadStatus = getStateIfFileExists(downloadToCheck);
			}
			return downloadStatus;
		});
	}

	Download getDownloadFromDb(Database database, int appId) {
		return getDownloadFromDb(database.getRealm(), appId);
	}

	Download getDownloadFromDb(Realm realm, int appId) {
		return realm.where(Download.class).equalTo("appId", appId).findFirst();
	}

	@NonNull
	DownloadState getStateIfFileExists(Download downloadToCheck) {
		DownloadState downloadStatus = DownloadState.COMPLETED;
		if (downloadToCheck != null) {
			for (RealmString path : downloadToCheck.getFilePaths()) {
				if (!FileUtils.fileExists(path.getString())) {
					downloadStatus = DownloadState.FILE_MISSING;
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
