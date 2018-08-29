package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.FileDownloadCallback;

/**
 * Created by filipegoncalves on 8/29/18.
 */

public class DownloadTaskStatus implements FileDownloadCallback {

  private final DownloadState state;

  public DownloadTaskStatus(DownloadState state) {
    this.state = state;
  }

  enum DownloadState {
    PENDING, PROGRESS, PAUSED, COMPLETED, ERROR, WARN
  }
}
