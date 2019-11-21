package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.logger.Logger;

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
        Logger.getInstance()
            .d("DownloadStatusMapper",
                "setting completed app download state to waiting to move files. So emittig a waiting to move files");
        downloadState = Download.WAITING_TO_MOVE_FILES;
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
      case ERROR_FILE_NOT_FOUND:
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
}
