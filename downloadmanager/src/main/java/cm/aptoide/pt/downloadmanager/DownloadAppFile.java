package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class DownloadAppFile {

  private String url;
  private String downloadMd5;
  public DownloadAppFile(String url, String downloadMd5) {
    this.url = url;
    this.downloadMd5 = downloadMd5;
  }
}
