package cm.aptoide.pt.downloadmanager;

public interface DownloadAnalytics {

  void onDownloadComplete(String md5, String packageName, int versionCode);

  void onError(String packageName, int versionCode, String md5, Throwable throwable);
}
