package cm.aptoide.pt.downloadmanager;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class DownloadAppFile {

  private String mainDownloadPath;
  private String alternativeDownloadPath;
  private String downloadMd5;
  private int versionCode;
  private String packageName;

  public DownloadAppFile(String mainDownloadPath, String alternativeDownloadPath,
      String downloadMd5, int versionCode, String packageName) {
    this.mainDownloadPath = mainDownloadPath;
    this.alternativeDownloadPath = alternativeDownloadPath;
    this.downloadMd5 = downloadMd5;
    this.versionCode = versionCode;
    this.packageName = packageName;
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

  public int getVersionCode() {
    return versionCode;
  }

  public String getPackageName() {
    return packageName;
  }
}
