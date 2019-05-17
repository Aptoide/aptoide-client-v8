package cm.aptoide.pt.downloadmanager;

import java.util.List;

/**
 * Created by filipegoncalves on 7/31/18.
 */

public class DownloadApp {

  private final String packageName;
  private final int versionCode;
  private List<DownloadAppFile> downloadFiles;
  private String md5;
  private long size;

  public DownloadApp(String packageName, int versionCode, List<DownloadAppFile> downloadFiles,
      String md5, long size) {
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.downloadFiles = downloadFiles;
    this.md5 = md5;
    this.size = size;
  }

  public List<DownloadAppFile> getDownloadFiles() {
    return downloadFiles;
  }

  public String getMd5() {
    return md5;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public long getSize() {
    return size;
  }
}
