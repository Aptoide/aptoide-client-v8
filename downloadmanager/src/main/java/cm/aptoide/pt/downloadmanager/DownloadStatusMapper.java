package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;

/**
 * Created by filipegoncalves on 9/4/18.
 */

public class DownloadStatusMapper {

  public int mapAppDownloadStatus(AppDownloadStatus.AppDownloadState appDownloadState) {
    int downloadState;
    switch (appDownloadState) {
      case PROGRESS:
        downloadState = RoomDownload.PROGRESS;
        break;
      case INVALID_STATUS:
        downloadState = RoomDownload.INVALID_STATUS;
        break;
      case VERIFYING_FILE_INTEGRITY:
        downloadState = RoomDownload.VERIFYING_FILE_INTEGRITY;
        break;
      case COMPLETED:
        downloadState = RoomDownload.COMPLETED;
        break;
      case PENDING:
        downloadState = RoomDownload.PENDING;
        break;
      case PAUSED:
        downloadState = RoomDownload.PAUSED;
        break;
      case WARN:
        downloadState = RoomDownload.WARN;
        break;
      case ERROR:
      case ERROR_MD5_DOES_NOT_MATCH:
      case ERROR_NOT_ENOUGH_SPACE:
      case ERROR_FILE_NOT_FOUND:
        downloadState = RoomDownload.ERROR;
        break;
      default:
        throw new IllegalArgumentException("Invalid app download state " + appDownloadState);
    }
    return downloadState;
  }

  public int mapDownloadError(AppDownloadStatus.AppDownloadState appDownloadState) {
    int downloadError;
    switch (appDownloadState) {
      case ERROR:
      case ERROR_MD5_DOES_NOT_MATCH:
      case ERROR_FILE_NOT_FOUND:
        downloadError = RoomDownload.GENERIC_ERROR;
        break;
      case ERROR_NOT_ENOUGH_SPACE:
        downloadError = RoomDownload.NOT_ENOUGH_SPACE_ERROR;
        break;
      default:
        downloadError = RoomDownload.NO_ERROR;
        break;
    }
    return downloadError;
  }
}
