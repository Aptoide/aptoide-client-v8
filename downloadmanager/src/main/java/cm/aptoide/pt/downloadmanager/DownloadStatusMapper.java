package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity;

/**
 * Created by filipegoncalves on 9/4/18.
 */

public class DownloadStatusMapper {

  public int mapAppDownloadStatus(AppDownloadStatus.AppDownloadState appDownloadState) {
    int downloadState;
    switch (appDownloadState) {
      case PROGRESS:
        downloadState = DownloadEntity.PROGRESS;
        break;
      case INVALID_STATUS:
        downloadState = DownloadEntity.INVALID_STATUS;
        break;
      case VERIFYING_FILE_INTEGRITY:
        downloadState = DownloadEntity.VERIFYING_FILE_INTEGRITY;
        break;
      case COMPLETED:
        downloadState = DownloadEntity.WAITING_TO_MOVE_FILES;
        break;
      case PENDING:
        downloadState = DownloadEntity.PENDING;
        break;
      case PAUSED:
        downloadState = DownloadEntity.PAUSED;
        break;
      case WARN:
        downloadState = DownloadEntity.WARN;
        break;
      case ERROR:
      case ERROR_MD5_DOES_NOT_MATCH:
      case ERROR_NOT_ENOUGH_SPACE:
      case ERROR_FILE_NOT_FOUND:
        downloadState = DownloadEntity.ERROR;
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
        downloadError = DownloadEntity.GENERIC_ERROR;
        break;
      case ERROR_NOT_ENOUGH_SPACE:
        downloadError = DownloadEntity.NOT_ENOUGH_SPACE_ERROR;
        break;
      default:
        downloadError = DownloadEntity.NO_ERROR;
        break;
    }
    return downloadError;
  }
}
