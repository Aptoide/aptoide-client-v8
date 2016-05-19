package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.RealmInteger;
import cm.aptoide.pt.database.realm.RealmString;
import cm.aptoide.pt.downloadmanager.model.DownloadState;
import cm.aptoide.pt.downloadmanager.model.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import io.realm.RealmList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.observables.ConnectableObservable;

/**
 * Created by trinkes on 5/13/16.
 */
public class DownloadTask extends FileDownloadListener {

	public static final int INTERVAL = 1000;    //interval between progress updates
	static public final int PROGRESS_MAX_VALUE = 100;
	public static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;
	private static final String TAG = DownloadTask.class.getSimpleName();

	final List<FileToDownload> downloads;
	private final int appId;
	/**
	 * Downloaded file path
	 */
	@Setter(AccessLevel.MODULE) @Getter String filePath;
	/**
	 * this boolean is used to change between serial and parallel download (in this downloadTask)
	 * the default value is true
	 */
	@Setter boolean isSerial = true;
	@Setter(AccessLevel.MODULE) @Getter private DownloadState status = DownloadState.STARTED;
	private ConnectableObservable<Integer> observable;

	public DownloadTask(int appId, List<FileToDownload> downloads) {
		this.downloads = downloads;
		this.appId = appId;

		this.observable = Observable.interval(INTERVAL / 4, INTERVAL, TimeUnit.MILLISECONDS)
				.map(aLong -> updateProgress(downloads))
				.filter(integer -> {
					if (integer <= 100 && status == DownloadState.PROGRESS) {
						if (integer == 100) {
							status = DownloadState.COMPLETED;
						}
						return true;
					} else {
						return false;
					}
				})
				.publish();
		observable.connect();

		startDownload();
	}

	/**
	 * Update the overall download progress. It updates the value on database and in memory list
	 *
	 * @param downloads files to download in this download
	 * @return new current progress
	 */
	@NonNull
	public Integer updateProgress(List<FileToDownload> downloads) {
		int progress = 0;
		RealmList<RealmInteger> downloadIds = new RealmList<>();
		for (int i = 0; i < downloads.size(); i++) {
			progress += downloads.get(i).getProgress();
			downloadIds.add(new RealmInteger(downloads.get(i).getDownloadId()));
		}

		Database database = AptoideDownloadManager.getInstance().getDatabase();
		Download download = new Download();
		download.setAppId(appId);
		download.setDownloadId(downloadIds);
		database.copyOrUpdate(download);
		database.close();
		return (int) Math.floor((float) progress / downloads.size());
	}

	public void startDownload() {
		Download downloadDb = new Download();
		RealmList<RealmInteger> downloadIdsToAdd = new RealmList<>();
		if (downloads != null) {
			for (FileToDownload download : downloads) {
				BaseDownloadTask baseDownloadTask = FileDownloader.getImpl()
						.create(download.getLink());
				baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, this);
				download.setDownloadId(baseDownloadTask.setListener(this)
						.setCallbackProgressTimes(PROGRESS_MAX_VALUE)
						.setPath(getFilePath(download))
						.ready());
				download.setAppId(appId);
				downloadDb.setAppId(appId);
				downloadIdsToAdd.add(new RealmInteger(download.getDownloadId()));
			}

			if (isSerial) {
				// To form a queue with the same queueTarget and execute them linearly
				FileDownloader.getImpl().start(this, true);
			} else {
				// To form a queue with the same queueTarget and execute them in parallel
				FileDownloader.getImpl().start(this, false);
			}
		}

		if (downloadIdsToAdd.size() > 0) {
			downloadDb.setDownloadId(downloadIdsToAdd);
		}

		AptoideDownloadManager.getInstance().getDatabase().copyOrUpdate(downloadDb);
	}

	private String getFilePath(FileToDownload download) {
		return download.getFileType().getPath() + download.getMd5();
	}

	public Observable<Integer> getObservable() {
		return observable;
	}

//	void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//		for (FileToDownload download : downloads) {
//			if (download.getInteger() == task.getId()) {
//				download.setProgress((int) Math.floor((float) soFarBytes / totalBytes *
//						DownloadTask.PROGRESS_MAX_VALUE));
//			}
//		}
//	}

//	void completed(BaseDownloadTask task) {
//		for (FileToDownload download : downloads) {
//			if (download.getInteger() == task.getId()) {
//				download.setProgress(DownloadTask.PROGRESS_MAX_VALUE);
//				download.setFilePath(task.getPath());
//			}
//		}
//	}

	@Override
	protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		Logger.d(TAG, "pending() called with: " + "task = [" + task + "], soFarBytes = [" +
				soFarBytes + "], totalBytes = [" + totalBytes + "]");
		getDownloadTask(task).setStatus(DownloadState.PENDING);
	}

	@Override
	protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		status = DownloadState.PROGRESS;
		for (FileToDownload download : downloads) {
			if (download.getDownloadId() == task.getId()) {
				download.setProgress((int) Math.floor((float) soFarBytes / totalBytes *
						DownloadTask.PROGRESS_MAX_VALUE));
			}
		}
	}

	@Override
	protected void completed(BaseDownloadTask task) {
		for (FileToDownload download : downloads) {
			if (download.getDownloadId() == task.getId()) {
				download.setProgress(DownloadTask.PROGRESS_MAX_VALUE);
				download.setFilePath(task.getPath());

				final int appId = download.getAppId();
				Database database = AptoideDownloadManager.getInstance().getDatabase();
				database.runTransaction((realm) -> {
					RealmString realmString = new RealmString(task.getPath());

					AptoideDownloadManager.getInstance()
							.getDownloadFromDb(realm, appId)
							.getFilePaths()
							.add(realm.copyToRealmOrUpdate(realmString));
				});
				database.close();
			}
		}
	}

	@Override
	protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		Logger.d(TAG, "paused() called with: " + "task = [" + task + "], soFarBytes = [" +
				soFarBytes + "], totalBytes = [" + totalBytes + "]");
		getDownloadTask(task).setStatus(DownloadState.PAUSED);
	}

	@Override
	protected void error(BaseDownloadTask task, Throwable e) {
		Logger.d(TAG, "error() called with: " + "task = [" + task + "], e = [" + e + "]");
		getDownloadTask(task).setStatus(DownloadState.ERROR);
	}

	@Override
	protected void warn(BaseDownloadTask task) {
		Logger.d(TAG, "warn() called with: " + "task = [" + task + "]");
	}

	private DownloadTask getDownloadTask(BaseDownloadTask task) {
		return (DownloadTask) task.getTag(DownloadTask.APTOIDE_DOWNLOAD_TASK_TAG_KEY);
	}
}
