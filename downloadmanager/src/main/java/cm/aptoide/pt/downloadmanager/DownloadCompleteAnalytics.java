package cm.aptoide.pt.downloadmanager;

public interface DownloadCompleteAnalytics {

  void onDownloadComplete(String md5, String packageName, int versionCode);
}
