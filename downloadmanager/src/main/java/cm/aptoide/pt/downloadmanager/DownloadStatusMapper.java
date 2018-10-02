package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import java.util.List;

/**
 * Created by filipegoncalves on 9/4/18.
 */

public class DownloadStatusMapper {

  public int mapAppDownloadStatus(AppDownloadStatus.AppDownloadState appDownloadState) {
    int downloadState;
    switch (appDownloadState) {
      case PROGRESS:
        downloadState = Download.PROGRESS;
        break;
      case INVALID_STATUS:
        downloadState = Download.INVALID_STATUS;
        break;
      case COMPLETED:
        downloadState = Download.COMPLETED;
        break;
      case PENDING:
        downloadState = Download.PENDING;
        break;
      case PAUSED:
        downloadState = Download.PAUSED;
        break;
      case WARN:
        downloadState = Download.WARN;
        break;
      case ERROR:
      case ERROR_NOT_ENOUGH_SPACE:
      case ERROR_FILE_NOT_FOUND:
        downloadState = Download.ERROR;
        break;
      default:
        throw new IllegalArgumentException("Invalid app download state");
    }
    return downloadState;
  }

  public int mapDownloadError(AppDownloadStatus.AppDownloadState appDownloadState) {
    int downloadError;
    switch (appDownloadState) {
      case ERROR:
        downloadError = Download.GENERIC_ERROR;
        break;
      case ERROR_NOT_ENOUGH_SPACE:
        downloadError = Download.NOT_ENOUGH_SPACE_ERROR;
        break;
      default:
        downloadError = Download.NO_ERROR;
        break;
    }
    return downloadError;
  }

  public int getAppDownloadStatus(List<FileDownloadCallback> fileDownloadCallbackList) {
    AppDownloadStatus.AppDownloadState previousState = null;
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      if (fileDownloadCallback.getDownloadState() == AppDownloadStatus.AppDownloadState.ERROR) {
        return Download.ERROR;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND) {
        return Download.ERROR;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.ERROR_NOT_ENOUGH_SPACE) {
        return Download.ERROR;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.WARN) {
        return Download.WARN;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.PAUSED) {
        return Download.PAUSED;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.INVALID_STATUS) {
        return Download.INVALID_STATUS;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.COMPLETED) {
        if (previousState != null
            && previousState != AppDownloadStatus.AppDownloadState.COMPLETED) {
          return Download.PROGRESS;
        } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
            == fileDownloadCallbackList.size() - 1) {
          return Download.COMPLETED;
        }
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.PENDING) {
        if (previousState != null && previousState != AppDownloadStatus.AppDownloadState.PENDING) {
          return Download.PROGRESS;
        } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
            == fileDownloadCallbackList.size() - 1) {
          return Download.PENDING;
        }
      }
      previousState = fileDownloadCallback.getDownloadState();
    }
    return Download.PROGRESS;
  }
}
