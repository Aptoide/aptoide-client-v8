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
      case COMPLETED:
        downloadState = Download.COMPLETED;
      case PENDING:
        downloadState = Download.PENDING;
      case PAUSED:
        downloadState = Download.PAUSED;
      case WARN:
        downloadState = Download.WARN;
      case ERROR:
        downloadState = Download.ERROR;
      default:
        throw new IllegalArgumentException("Invalid app download state");
    }
  }
}
