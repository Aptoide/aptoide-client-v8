package cm.aptoide.pt.downloadmanager;

public interface DownloadErrorAnalytics {

  void onError(String packageName, int versionCode, Throwable throwable);
}
