package cm.aptoide.pt.downloadmanager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import lombok.Cleanup;
import lombok.Setter;
import rx.Observable;
import rx.observables.ConnectableObservable;

/**
 * Created by trinkes on 5/13/16.
 */
public class DownloadTask extends FileDownloadLargeFileListener {

	public static final int INTERVAL = 1000;    //interval between progress updates
	static public final int PROGRESS_MAX_VALUE = 100;
	public static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;
	private static final String TAG = DownloadTask.class.getSimpleName();

	final Download download;
	private final long appId;
	/**
	 * this boolean is used to change between serial and parallel download (in this downloadTask) the default value is
	 * true
	 */
	@Setter boolean isSerial = true;
	private ConnectableObservable<Integer> observable;

	public DownloadTask(Download download) {
		this.download = download;
		this.appId = download.getAppId();

		this.observable = Observable.interval(INTERVAL / 4, INTERVAL, TimeUnit.MILLISECONDS)
				.map(aLong -> updateProgress())
				.filter(integer -> {
					download.setOverallProgress(integer);
					if (integer <= 100 && download.getOverallDownloadStatus() == Download.PROGRESS) {
						if (integer == 100) {
							download.setOverallDownloadStatus(Download.COMPLETED);
						}
						return true;
					} else {
						return false;
					}
				})
				.publish();
		observable.connect();
	}

	@NonNull
	static String getFilePathFromFileType(FileToDownload fileToDownload) {
		String path;
		switch (fileToDownload.getFileType()) {
			case FileToDownload.APK:
				path = AptoideDownloadManager.APK_PATH;
				break;
			case FileToDownload.OBB:
				path = AptoideDownloadManager.OBB_PATH + fileToDownload.getPackageName();
				break;
			default:
				path = AptoideDownloadManager.GENERIC_PATH;
				break;
		}
		return path;
	}

	/**
	 * Update the overall download progress. It updates the value on database and in memory list
	 *
	 * @return new current progress
	 */
	@NonNull
	public Integer updateProgress() {
		if (download.getOverallProgress() >= 100) {
			return download.getOverallProgress();
		}

		int progress = 0;
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			progress += fileToDownload.getProgress();
		}
		download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload().size()));
		saveDownloadInDb(download);
		return download.getOverallProgress();
	}

	/**
	 * @throws IllegalArgumentException
	 */
	public void startDownload() throws IllegalArgumentException {
		if (download.getFilesToDownload() != null) {
			for (FileToDownload fileToDownload : download.getFilesToDownload()) {
				if (TextUtils.isEmpty(fileToDownload.getLink())) {
					throw new IllegalArgumentException("A link to download must be provided");
				}
				BaseDownloadTask baseDownloadTask = FileDownloader.getImpl().create(fileToDownload.getLink());
				baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, this);
				fileToDownload.setDownloadId(baseDownloadTask.setListener(this)
						.setCallbackProgressTimes(PROGRESS_MAX_VALUE)
						.setPath(AptoideDownloadManager.DOWNLOADS_STORAGE_PATH + fileToDownload.getFileName())
						.ready());
				fileToDownload.setAppId(appId);
			}

			if (isSerial) {
				// To form a queue with the same queueTarget and execute them linearly
				FileDownloader.getImpl().start(this, true);
			} else {
				// To form a queue with the same queueTarget and execute them in parallel
				FileDownloader.getImpl().start(this, false);
			}
		}
		saveDownloadInDb(download);
	}

	private void saveDownloadInDb(Download download) {
		@Cleanup Realm realm = Database.get();
		Database.save(download, realm);
	}

	public Observable<Integer> getObservable() {
		return observable;
	}

	@Override
	protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		Logger.d(TAG, "pending() called with: " + "task = [" + task + "], soFarBytes = [" +
				soFarBytes + "], totalBytes = [" + totalBytes + "]");
		download.setOverallDownloadStatus(Download.PENDING);
		saveDownloadInDb(download);
	}

	@Override
	protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		progress(task, (long) soFarBytes, (long) totalBytes);
	}

	@Override
	protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		Logger.d(TAG, "paused() called with: " + "task = [" + task + "], soFarBytes = [" +
				soFarBytes + "], totalBytes = [" + totalBytes + "]");
		download.setOverallDownloadStatus(Download.PAUSED);
	}

	@Override
	protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		Logger.d(TAG, "pending() called with: " + "task = [" + task + "], soFarBytes = [" + soFarBytes + "], " +
				"totalBytes = [" + totalBytes + "]");
	}

	@Override
	protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		Logger.d(TAG, "progress() called with: " + "task = [" + task + "], soFarBytes = [" + soFarBytes + "], " +
				"totalBytes = [" + totalBytes + "]");
		download.setOverallDownloadStatus(Download.PROGRESS);
		for (FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (fileToDownload.getDownloadId() == task.getId()) {
				fileToDownload.setProgress((int) Math.floor((float) soFarBytes / totalBytes * DownloadTask
						.PROGRESS_MAX_VALUE));
			}
		}
		saveDownloadInDb(download);
	}

	@Override
	protected void blockComplete(BaseDownloadTask task) {
		Logger.d(TAG, "blockComplete() called with: " + "task = [" + task + "]");
	}

	@Override
	protected void completed(BaseDownloadTask task) {
		Logger.d(TAG, "completed() called with: " + "task = [" + task + "]");
		for (FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (fileToDownload.getDownloadId() == task.getId()) {
				fileToDownload.setPath(getFilePathFromFileType(fileToDownload));
				moveFileToRightPlace(fileToDownload);
				fileToDownload.setProgress(DownloadTask.PROGRESS_MAX_VALUE);
			}
		}
		saveDownloadInDb(download);
	}

	@Override
	protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		Logger.d(TAG, "paused() called with: " + "task = [" + task + "], soFarBytes = [" + soFarBytes + "], " +
				"totalBytes" +
				" = [" + totalBytes + "]");
		saveDownloadInDb(download);
	}

	@Override
	protected void error(BaseDownloadTask task, Throwable e) {
		Logger.d(TAG, "error() called with: " + "task = [" + task + "], e = [" + e + "]");
		Logger.printException(e);
		AptoideDownloadManager.getInstance().stopDownload(download.getAppId());
		download.setOverallDownloadStatus(Download.ERROR);
		saveDownloadInDb(download);
	}

	@Override
	protected void warn(BaseDownloadTask task) {
		Logger.d(TAG, "warn() called with: " + "task = [" + task + "]");
	}

	private void moveFileToRightPlace(FileToDownload fileToDownload) {
		if (!FileUtils.copyFile(AptoideDownloadManager.DOWNLOADS_STORAGE_PATH, fileToDownload.getPath(),
				fileToDownload.getFileName())) {
			download.setOverallDownloadStatus(Download.ERROR);
			AptoideDownloadManager.getInstance().stopDownload(download.getAppId());
		}
	}
}
