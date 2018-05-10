package cm.aptoide.pt.app;

/**
 * Created by filipegoncalves on 5/9/18.
 */

public class DownloadAppViewModel {

  private final Action action;
  private final long progress;
  private final DownloadState downloadState;

  public DownloadAppViewModel(Action action, long progress, DownloadState downloadState) {
    this.action = action;
    this.progress = progress;
    this.downloadState = downloadState;
  }

  public Action getAction() {
    return action;
  }

  public long getProgress() {
    return progress;
  }

  public DownloadState getDownloadState() {
    return downloadState;
  }

  public boolean isDownloading() {
    return downloadState.equals(DownloadState.ACTIVE)
        || downloadState.equals(DownloadState.PAUSE)
        || downloadState.equals(DownloadState.INDETERMINATE);
  }

  public enum Error {
    NETWORK, GENERIC
  }

  public enum Action {
    UPDATE, INSTALL, DOWNGRADE, OPEN
  }

  public enum DownloadState {
    ACTIVE, PAUSE, COMPLETE, INDETERMINATE, ERROR
  }
}
