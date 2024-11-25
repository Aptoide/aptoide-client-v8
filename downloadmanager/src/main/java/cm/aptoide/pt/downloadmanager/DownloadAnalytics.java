package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomDownload;

public interface DownloadAnalytics {

  void onDownloadComplete(String md5, String packageName, int versionCode);

  void onError(String packageName, int versionCode, String md5, Throwable throwable,
      String downloadErrorUrl, String downloadHttpError);

  void startProgress(RoomDownload download);
}
