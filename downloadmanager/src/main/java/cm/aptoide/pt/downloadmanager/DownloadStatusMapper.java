package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;

/**
 * Created by filipegoncalves on 9/4/18.
 */

public class DownloadStatusMapper {

  public int mapAppDownloadStatus(AppDownloadStatus.AppDownloadState appDownloadState) {
    int downloadState;
    switch (appDownloadState) {
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
        downloadState = Download.ERROR;
        break;
      default:
        throw new IllegalArgumentException("Invalid app download state");
    }
    return downloadState;
  }
}
