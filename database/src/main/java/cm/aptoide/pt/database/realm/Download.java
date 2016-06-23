/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sithengineer on 17/05/16.
 */
public class Download extends RealmObject {

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
	RealmList<FileToDownload> filesToDownload;
	@DownloadState int overallDownloadStatus = 0;
	int overallProgress = 0;
	@PrimaryKey private long appId;

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

	@IntDef({INVALID_STATUS, COMPLETED, BLOCK_COMPLETE, CONNECTED, PENDING, PROGRESS, PAUSED, WARN, STARTED, ERROR,
			FILE_MISSING, RETRY, NOT_DOWNLOADED})
	@Retention(RetentionPolicy.SOURCE)

	public @interface DownloadState {

	}
}
