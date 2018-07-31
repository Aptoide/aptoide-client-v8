package cm.aptoide.pt.downloadmanager;

import java.util.List;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class DownloadApp {

  private List<DownloadAppFile> downloadFiles;

  public DownloadApp(List<DownloadAppFile> downloadFiles) {
    this.downloadFiles = downloadFiles;
  }

  public List<DownloadAppFile> getDownloadFiles() {
    return downloadFiles;
  }
}
