/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;

import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.utils.FileUtils;
import io.realm.Realm;
import lombok.Cleanup;
import lombok.Setter;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 5/13/16.
 */
public class DownloadTask extends FileDownloadLargeFileListener {

	public static final int INTERVAL = 1000;    //interval between progress updates
	public static final int APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888;
	private static final String TAG = DownloadTask.class.getSimpleName();

	final Download download;
	private final long appId;
	/**
	 * this boolean is used to change between serial and parallel download (in this downloadTask) the default value is
	 * true
	 */
	@Setter boolean isSerial = true;
	private ConnectableObservable<Download> observable;

	public DownloadTask(Download download) {
		this.download = download;
		this.appId = download.getAppId();

		this.observable = Observable.interval(INTERVAL / 4, INTERVAL, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.takeUntil(integer1 -> download.getOverallDownloadStatus() != Download.PROGRESS && download.getOverallDownloadStatus() != Download.IN_QUEUE &&
						download.getOverallDownloadStatus() != Download.PENDING)
				.filter(aLong1 -> download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.COMPLETED)
				.map(aLong -> updateProgress())
				.filter(updatedDownload -> {
					if (updatedDownload.getOverallProgress() <= AptoideDownloadManager.PROGRESS_MAX_VALUE && download
							.getOverallDownloadStatus() == Download.PROGRESS) {
						if (updatedDownload.getOverallProgress() == AptoideDownloadManager.PROGRESS_MAX_VALUE && download.getOverallDownloadStatus() !=
								Download.COMPLETED) {
							setDownloadStatus(Download.COMPLETED, download);
							AptoideDownloadManager.getInstance().currentDownloadFinished(download.getAppId());
						}
						return true;
					} else {
						return false;
					}
				})
				.publish();
	}

	@NonNull
	static String getFilePathFromFileType(FileToDownload fileToDownload) {
		String path;
		switch (fileToDownload.getFileType()) {
			case FileToDownload.APK:
				path = AptoideDownloadManager.APK_PATH;
				break;
			case FileToDownload.OBB:
				path = AptoideDownloadManager.OBB_PATH + fileToDownload.getPackageName() + "/";
				break;
			case FileToDownload.GENERIC:
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
	public Download updateProgress() {
		if (download.getOverallProgress() >= AptoideDownloadManager.PROGRESS_MAX_VALUE || download.getOverallDownloadStatus() != Download
				.PROGRESS) {
			return download;
		}

		int progress = 0;
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			progress += fileToDownload.getProgress();
		}
		download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload().size()));
		saveDownloadInDb(download);
		return download;
	}

	/**
	 * @throws IllegalArgumentException
	 */
	public void startDownload() throws IllegalArgumentException {
		observable.connect();
		if (download.getFilesToDownload() != null) {
			for (FileToDownload fileToDownload : download.getFilesToDownload()) {
				if (TextUtils.isEmpty(fileToDownload.getLink())) {
					throw new IllegalArgumentException("A link to download must be provided");
				}
				BaseDownloadTask baseDownloadTask = FileDownloader.getImpl().create(fileToDownload.getLink());
				baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, this);
				fileToDownload.setDownloadId(baseDownloadTask.setListener(this).setCallbackProgressTimes(AptoideDownloadManager.PROGRESS_MAX_VALUE)
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


	private synchronized void saveDownloadInDb(Download download) {
		Observable.fromCallable(() -> {
			Realm realm = DeprecatedDatabase.get();
			DeprecatedDatabase.save(download, realm);
			realm.close();
			return null;
		}).subscribeOn(Schedulers.io()).subscribe();
	}

	public Observable<Download> getObservable() {
		return observable;
	}

	@Override
	protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		pending(task, (long) soFarBytes, (long) totalBytes);
		setDownloadStatus(Download.PENDING, download, task);
	}

	@Override
	protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		progress(task, (long) soFarBytes, (long) totalBytes);
	}

	@Override
	protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
		paused(task, (long) soFarBytes, (long) totalBytes);
	}

	@Override
	protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		setDownloadStatus(Download.PENDING, download, task);
	}

	@Override
	protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		for (FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (fileToDownload.getDownloadId() == task.getId()) {
				//sometimes to totalBytes = 0, i believe that's when a 301(Moved Permanently) http error occurs
				if (totalBytes > 0) {
					fileToDownload.setProgress((int) Math.floor((float) soFarBytes / totalBytes * AptoideDownloadManager.PROGRESS_MAX_VALUE));
				} else {
					fileToDownload.setProgress(0);
				}
			}
		}
		this.download.setDownloadSpeed(task.getSpeed() * 1024);
		if (download.getOverallDownloadStatus() != Download.PROGRESS) {
			setDownloadStatus(Download.PROGRESS, download, task);
		}
	}

	@Override
	protected void blockComplete(BaseDownloadTask task) {

	}

	@Override
	protected void completed(BaseDownloadTask task) {
		Observable.from(download.getFilesToDownload())
				.filter(file -> file.getDownloadId() == task.getId())
				.flatMap(file -> {
					file.setPath(getFilePathFromFileType(file));
					file.setStatus(Download.COMPLETED);
					return moveFileToRightPlace(download).doOnNext(fileMoved -> file.setProgress(AptoideDownloadManager.PROGRESS_MAX_VALUE));
				})
				.doOnUnsubscribe(() -> AptoideDownloadManager.getInstance().setDownloading(false))
				.subscribeOn(Schedulers.io())
				.subscribe(success -> saveDownloadInDb(download), throwable -> setDownloadStatus(Download.ERROR, download));
		download.setDownloadSpeed(task.getSpeed() * 1024);
	}

	@Override
	protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		setDownloadStatus(Download.PAUSED, download, task);
		AptoideDownloadManager.getInstance().currentDownloadFinished(download.getAppId());
	}

	@Override
	protected void error(BaseDownloadTask task, Throwable e) {
		AptoideDownloadManager.getInstance().pauseDownload(download.getAppId());
		if (e instanceof FileDownloadHttpException && ((FileDownloadHttpException) e).getCode() == 404) {
			for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
				if (!TextUtils.isEmpty(fileToDownload.getAltLink())) {
					fileToDownload.setLink(fileToDownload.getAltLink());
					fileToDownload.setAltLink(null);
					@Cleanup Realm realm = DeprecatedDatabase.get();
					DeprecatedDatabase.save(download, realm);
					Intent intent = new Intent(AptoideDownloadManager.getContext(), NotificationEventReceiver.class);
					intent.setAction(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
					intent.putExtra(AptoideDownloadManager.APP_ID_EXTRA, download.getAppId());
					AptoideDownloadManager.getContext().sendBroadcast(intent);
					return;
				}
			}
		}
		setDownloadStatus(Download.ERROR, download, task);
	}

	@Override
	protected void warn(BaseDownloadTask task) {
		setDownloadStatus(Download.WARN, download, task);
	}


	private void setDownloadStatus(@Download.DownloadState int status, Download download) {
		setDownloadStatus(status, download, null);
	}

	private void setDownloadStatus(@Download.DownloadState int status, Download download, @Nullable BaseDownloadTask
			task) {
		if (task != null) {
			for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
				if (fileToDownload.getDownloadId() == task.getId()) {
					fileToDownload.setStatus(status);
				}
			}
		}

		this.download.setOverallDownloadStatus(status);
		saveDownloadInDb(download);
		if (status == Download.PROGRESS || status == Download.PENDING) {
			AptoideDownloadManager.getInstance().setDownloading(true);
		} else {
			AptoideDownloadManager.getInstance().setDownloading(false);
		}
	}

	private Observable<Void> moveFileToRightPlace(Download download) {
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (fileToDownload.getStatus() != Download.COMPLETED) {
				return Observable.just(null);
			}
		}
		return Observable.fromCallable(() -> {
			for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
				FileUtils.copyFile(AptoideDownloadManager.DOWNLOADS_STORAGE_PATH, fileToDownload.getPath(), fileToDownload.getFileName());
			}
			return null;
		});
	}
}
