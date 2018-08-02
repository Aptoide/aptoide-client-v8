package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class DownloadAppFile {

  private String mainDownloadPath;
  private String alternativeDownloadPath;
  private String downloadMd5;

  public DownloadAppFile(String mainDownloadPath, String alternativeDownloadPath,
      String downloadMd5) {
    this.mainDownloadPath = mainDownloadPath;
    this.alternativeDownloadPath = alternativeDownloadPath;
    this.downloadMd5 = downloadMd5;
  }

  public String getMainDownloadPath() {
    return mainDownloadPath;
  }

  public String getAlternativeDownloadPath() {
    return alternativeDownloadPath;
  }

  public String getDownloadMd5() {
    return downloadMd5;
  }
}
