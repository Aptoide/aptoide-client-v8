package cm.aptoide.pt.downloadmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
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
	NotificationCompat.Builder builder;
	Intent pauseDownloadsIntent;
	Intent openAppsManagerIntent;
	Intent resumeDownloadIntent;
	Intent notificationClickIntent;
	private ConnectableObservable<Download> observable;
	private NotificationManager notificationManager;

	public DownloadTask(Download download) {
		this.download = download;
		this.appId = download.getAppId();

		this.observable = Observable.interval(INTERVAL / 4, INTERVAL, TimeUnit.MILLISECONDS)
				.map(aLong -> updateProgress()).subscribeOn(Schedulers.io()).filter(updatedDownload -> {
					if (updatedDownload.getOverallProgress() <= PROGRESS_MAX_VALUE && download
							.getOverallDownloadStatus() == Download.PROGRESS) {
						if (updatedDownload.getOverallProgress() == PROGRESS_MAX_VALUE && download
								.getOverallDownloadStatus() != Download
								.COMPLETED) {
							setDownloadStatus(Download.COMPLETED, download);
							removeNotification(download);
							AptoideDownloadManager.getInstance().currentDownloadFinished(download.getAppId());
						}
						return true;
					} else {
						return false;
					}
				}).subscribeOn(Schedulers.io())
//				.takeUntil(integer1 -> download.getOverallDownloadStatus() != Download.COMPLETED)
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
			case FileToDownload.GENERIC:
			default:
				path = AptoideDownloadManager.GENERIC_PATH;
				break;
		}
		return path;
	}

	private void removeNotification(Download download) {
		notificationManager.cancel(download.getFilesToDownload().get(0).getDownloadId());
	}

	/**
	 * Update the overall download progress. It updates the value on database and in memory list
	 *
	 * @return new current progress
	 */
	@NonNull
	public Download updateProgress() {
		if (download.getOverallProgress() >= PROGRESS_MAX_VALUE || download.getOverallDownloadStatus() != Download
				.PROGRESS) {
			return download;
		}

		int progress = 0;
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			progress += fileToDownload.getProgress();
		}
		download.setOverallProgress((int) Math.floor((float) progress / download.getFilesToDownload().size()));
		saveDownloadInDb(download);
		updateNotification(download);
		return download;
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
		buildNotification();
		saveDownloadInDb(download);
	}

	private void buildNotification() {
		Bundle bundle = new Bundle();
		bundle.putLong(NotificationEventReceiver.APP_ID_EXTRA, download.getAppId());
		notificationClickIntent = createNotificationIntent(NotificationEventReceiver
				.DOWNLOADMANAGER_ACTION_NOTIFICATION, bundle);
		pauseDownloadsIntent = createNotificationIntent(NotificationEventReceiver.DOWNLOADMANAGER_ACTION_PAUSE, null);
		openAppsManagerIntent = createNotificationIntent(NotificationEventReceiver.DOWNLOADMANAGER_ACTION_OPEN, null);

		bundle = new Bundle();
		bundle.putLong(NotificationEventReceiver.APP_ID_EXTRA, download.getAppId());
		resumeDownloadIntent = createNotificationIntent(NotificationEventReceiver.DOWNLOADMANAGER_ACTION_RESUME,
				bundle);

		PendingIntent pPause = getPendingIntent(pauseDownloadsIntent);
		PendingIntent pOpenAppsManager = getPendingIntent(openAppsManagerIntent);
		PendingIntent pNotificationClick = getPendingIntent(notificationClickIntent);

		builder = new NotificationCompat.Builder(AptoideDownloadManager.getContext()).setSmallIcon
				(AptoideDownloadManager
				.getInstance()
				.getNotificationInterface()
				.getMainIcon())
				.setAutoCancel(false)
				.setOngoing(true)
				.setContentTitle(String.format(AptoideDownloadManager.getContext()
						.getResources()
						.getString(R.string.aptoide_downloading), Application.getConfiguration().getMarketName()))
				.setContentText(new StringBuilder().append(download.getAppName())
						.append(Download.getStatusName(download.getOverallDownloadStatus(), AptoideDownloadManager
								.getContext())))
				.setContentIntent(pNotificationClick)
				.setProgress(PROGRESS_MAX_VALUE, 0, false)
				.addAction(android.R.drawable.ic_menu_edit, AptoideDownloadManager.getContext()
						.getString(R.string.pause_download), pPause)
				.addAction(android.R.drawable.ic_menu_edit, AptoideDownloadManager.getContext()
						.getString(R.string.open_apps_manager), pOpenAppsManager);
		Notification notification = builder.build();

		notificationManager = (NotificationManager) AptoideDownloadManager.getContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(download.getFilesToDownload().get(0).getDownloadId(), notification);
	}

	private PendingIntent getPendingIntent(Intent intent) {
		return PendingIntent.getBroadcast(AptoideDownloadManager.getContext(), download.getFilesToDownload()
				.get(0)
				.getDownloadId(), intent, 0);
	}

	private Intent createNotificationIntent(String intentAction, @Nullable Bundle bundle) {
		Intent intent = new Intent(AptoideDownloadManager.getContext(), NotificationEventReceiver.class);
		intent.setAction(intentAction);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		return intent;
	}

	synchronized private void updateNotification(Download download1) {
		if (notificationManager != null) {
			boolean isOngoing = ((builder.build().flags & Notification.FLAG_ONGOING_EVENT) == Notification
					.FLAG_ONGOING_EVENT);
			boolean isToggled = false;
			if (download1.getOverallDownloadStatus() == Download.PROGRESS && !isOngoing) {
				isOngoing = !isOngoing;
				isToggled = true;
			} else if (download1.getOverallDownloadStatus() != Download.PROGRESS && isOngoing) {
				isOngoing = !isOngoing;
				isToggled = true;
			}
			if (isToggled || download1.getOverallDownloadStatus() == Download.PROGRESS) {
				builder.setProgress(PROGRESS_MAX_VALUE, this.download.getOverallProgress(), false)
						.setContentText(new StringBuilder().append(download1.getAppName())
								.append(Download.getStatusName(download1.getOverallDownloadStatus(),
										AptoideDownloadManager
										.getContext())));
				if (isToggled) {
					builder.setOngoing(isOngoing);
				}
				notificationManager.notify(download1.getFilesToDownload().get(0).getDownloadId(), builder.build());
			}
		}
	}

	private void saveDownloadInDb(Download download) {
		@Cleanup Realm realm = Database.get();
		Database.save(download, realm);
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
		Logger.d(TAG, "pending() called with: " + "task = [" + task + "], soFarBytes = [" + soFarBytes + "], " +
				"totalBytes = [" + totalBytes + "]");
		setDownloadStatus(Download.PENDING, download, task);
	}

	@Override
	protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		Logger.d(TAG, "progress() called with: " + "task = [" + task + "], soFarBytes = [" + soFarBytes + "], " +
				"totalBytes = [" + totalBytes + "]");
		for (FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (fileToDownload.getDownloadId() == task.getId()) {
				fileToDownload.setProgress((int) Math.floor((float) soFarBytes / totalBytes * DownloadTask
						.PROGRESS_MAX_VALUE));
			}
		}
		setDownloadStatus(Download.PROGRESS, download, task);
		AptoideDownloadManager.getInstance().setDownloading(true);
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
				fileToDownload.setStatus(Download.COMPLETED);
				moveFileToRightPlace(download);
				fileToDownload.setProgress(DownloadTask.PROGRESS_MAX_VALUE);
			}
		}
		saveDownloadInDb(download);
		AptoideDownloadManager.getInstance().setDownloading(false);
	}

	@Override
	protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
		Logger.d(TAG, "paused() called with: " + "task = [" + task + "], soFarBytes = [" + soFarBytes + "], " +
				"totalBytes" +
				" = [" + totalBytes + "]");
		setDownloadStatus(Download.PAUSED, download, task);
		setupOnDownloadPausedNotification(download);
	}

	@Override
	protected void error(BaseDownloadTask task, Throwable e) {
		Logger.d(TAG, "error() called with: " + "task = [" + task + "], e = [" + e + "]");
		Logger.printException(e);
		AptoideDownloadManager.getInstance().pauseDownload(download.getAppId());
		setDownloadStatus(Download.ERROR, download, task);
		setupOnDownloadPausedNotification(download);
	}

	@Override
	protected void warn(BaseDownloadTask task) {
		Logger.d(TAG, "warn() called with: " + "task = [" + task + "]");
		setDownloadStatus(Download.WARN, download, task);
	}

	private void setupOnDownloadPausedNotification(Download downloadToStop) {
		PendingIntent pResume = getPendingIntent(resumeDownloadIntent);
		builder.mActions.get(0).title = AptoideDownloadManager.getContext().getString(R.string.resume_download);
		builder.mActions.get(0).actionIntent = pResume;
		updateNotification(downloadToStop);
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
		if (status == Download.PROGRESS) {
			AptoideDownloadManager.getInstance().setDownloading(true);
		} else {
			AptoideDownloadManager.getInstance().setDownloading(false);
		}
	}

	private void moveFileToRightPlace(Download download) {
		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (fileToDownload.getStatus() != Download.COMPLETED) {
				return;
			}
		}

		for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
			if (!FileUtils.copyFile(AptoideDownloadManager.DOWNLOADS_STORAGE_PATH, fileToDownload.getPath(),
					fileToDownload
					.getFileName())) {
				setDownloadStatus(Download.ERROR, download);
				AptoideDownloadManager.getInstance().pauseDownload(download.getAppId());
			}
		}
	}
}
