package cm.aptoide.pt.downloadmanager.model;

import com.liulishuo.filedownloader.model.FileDownloadStatus;

/**
 * Created by trinkes on 5/16/16.
 */
public enum DownloadState {
	INVALID_STATUS,
	COMPLETED,
	BLOCK_COMPLETE,
	CONNECTED,
	PENDING,
	PROGRESS,
	PAUSED,
	WARN,
	STARTED,
	ERROR,
	FILE_MISSING,
	RETRY,
	NOT_DOWNLOADED;

	public static DownloadState getEnumState(int state) {
		DownloadState status = DownloadState.INVALID_STATUS;
		switch (state) {
			case FileDownloadStatus.INVALID_STATUS:
				break;
			case FileDownloadStatus.completed:
				status = COMPLETED;
				break;
			case FileDownloadStatus.blockComplete:
				status = BLOCK_COMPLETE;
				break;
			case FileDownloadStatus.connected:
				status = CONNECTED;
				break;
			case FileDownloadStatus.pending:
				status = PENDING;
				break;
			case FileDownloadStatus.progress:
				status = PROGRESS;
				break;
			case FileDownloadStatus.paused:
				status = PAUSED;
				break;
			case FileDownloadStatus.warn:
				status = WARN;
				break;
			case FileDownloadStatus.started:
				status = STARTED;
				break;
			case FileDownloadStatus.error:
				status = ERROR;
				break;
			case FileDownloadStatus.retry:
				status = RETRY;
				break;
		}
		return status;
	}
}
