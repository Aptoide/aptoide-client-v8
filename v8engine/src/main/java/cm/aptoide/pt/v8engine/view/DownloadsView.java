package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.database.realm.Download;
import java.util.List;

public interface DownloadsView extends View {
  //void showActiveDownloads(List<DownloadViewModel> downloads);
  //void showStandByDownloads(List<DownloadViewModel> downloads);
  //void showCompletedDownloads(List<DownloadViewModel> downloads);
  void showActiveDownloads(List<Download> downloads);
  void showStandByDownloads(List<Download> downloads);
  void showCompletedDownloads(List<Download> downloads);

  void showEmptyDownloadList();

  class DownloadViewModel {

    public enum Status {
      DOWNLOADING, STAND_BY, COMPLETED;
    }

    private final int progress;
    private final String appMd5;
    private final String appName;
    private final Status status;

    public DownloadViewModel(int progress, String appMd5, String appName, Status status) {
      this.progress = progress;
      this.appMd5 = appMd5;
      this.appName = appName;
      this.status = status;
    }

    public int getProgress() {
      return progress;
    }

    public String getAppName() {
      return appName;
    }

    public Status getStatus() {
      return status;
    }
  }
}
