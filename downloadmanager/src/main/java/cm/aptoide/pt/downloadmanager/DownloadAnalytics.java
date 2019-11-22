package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;

public interface DownloadAnalytics {

  void onDownloadComplete(String md5, String packageName, int versionCode);

  void onError(String packageName, int versionCode, String md5, Throwable throwable);

  void startProgress(Download download);
}
