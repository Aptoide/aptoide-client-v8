package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;

public interface DownloadAnalytics {

  void onDownloadComplete(String md5, String packageName, int versionCode,
      int averageDownloadSpeed);

  void onError(String packageName, int versionCode, String md5, Throwable throwable,
      String downloadErrorUrl, String downloadHttpError, int averageDownloadSpeed);

  void startProgress(RoomDownload download);

  void onDownloadCancel(String md5, int averageDownloadSpeed);
}
