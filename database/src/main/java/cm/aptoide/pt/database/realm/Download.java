/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import android.content.Context;
import android.support.annotation.IntDef;
import cm.aptoide.pt.database.R;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

/**
 * Created by sithengineer on 17/05/16.
 */
public class Download extends RealmObject {

	public static final String DOWNLOAD_ID = "appId";
	public static final int INVALID_STATUS = 0;
	public static final int COMPLETED = 1;
	public static final int BLOCK_COMPLETE = 2;
	public static final int CONNECTED = 3;
	public static final int PENDING = 4;
	public static final int PROGRESS = 5;
	public static final int PAUSED = 6;
	public static final int WARN = 7;
	public static final int STARTED = 8;
	public static final int ERROR = 9;
	public static final int FILE_MISSING = 10;
	public static final int RETRY = 11;
	public static final int NOT_DOWNLOADED = 12;
	public static final int IN_QUEUE = 13;
	public static final int ASCENDING = 1;
	public static final int DESCENDING = -1;
	public static String TAG = Download.class.getSimpleName();
	RealmList<FileToDownload> filesToDownload;
	@DownloadState int overallDownloadStatus = 0;
	int overallProgress = 0;
	@PrimaryKey private long appId;
	private String appName;
	private String Icon;
	@SuppressWarnings({"all"}) private long timeStamp;
	private int downloadSpeed;
	public Download() {
	}

	/**
	 * This method sorts the downloads by time stamp
	 *
	 * @param downloads list of downloads to sort
	 * @param sortOrder 1 if should be sorted ASCENDING, -1 if DESCENDING
	 */
	public static List<Download> sortDownloads(List<Download> downloads,
			@DownloadSort int sortOrder) {
		Collections.sort(downloads,
				(lhs, rhs) -> Long.valueOf(lhs.getTimeStamp()).compareTo(rhs.getTimeStamp()) * sortOrder);
		return downloads;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getStatusName(Context context) {
		String toReturn;
		switch (overallDownloadStatus) {
			case COMPLETED:
				toReturn = context.getString(R.string.download_completed);
				break;
			case PAUSED:
				toReturn = context.getString(R.string.download_paused);
				break;
			case PROGRESS:
				toReturn = context.getString(R.string.download_progress);
				break;
			case PENDING:
			case IN_QUEUE:
				toReturn = context.getString(R.string.download_queue);
				break;
			case INVALID_STATUS:
				toReturn = ""; //this state only appears while download manager doesn't get the download(before the AptoideDownloadManager#startDownload
				// method runs)
				break;
			case WARN:
			case BLOCK_COMPLETE:
			case CONNECTED:
			case RETRY:
			case STARTED:
			case NOT_DOWNLOADED:
			case ERROR:
			case FILE_MISSING:
			default:
				toReturn = context.getString(R.string.simple_error_occured);
		}
		return toReturn;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public RealmList<FileToDownload> getFilesToDownload() {
		return filesToDownload;
	}

	public void setFilesToDownload(RealmList<FileToDownload> filesToDownload) {
		this.filesToDownload = filesToDownload;
	}

	public
	@DownloadState
	int getOverallDownloadStatus() {
		return overallDownloadStatus;
	}

	public void setOverallDownloadStatus(@DownloadState int overallDownloadStatus) {
		this.overallDownloadStatus = overallDownloadStatus;
	}

	public int getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(int overallProgress) {
		this.overallProgress = overallProgress;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String getIcon() {
		return Icon;
	}

	public void setIcon(String icon) {
		Icon = icon;
	}

	public int getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(int speed) {
		this.downloadSpeed = speed;
	}

	@IntDef({INVALID_STATUS, COMPLETED, BLOCK_COMPLETE, CONNECTED, PENDING, PROGRESS, PAUSED, WARN, STARTED, ERROR, FILE_MISSING, RETRY, NOT_DOWNLOADED,
			IN_QUEUE})

	@Retention(RetentionPolicy.SOURCE)

	public @interface DownloadState {

	}

	@IntDef({
			ASCENDING, DESCENDING
	}) @Retention(RetentionPolicy.SOURCE)

	public @interface DownloadSort {
	}
}
