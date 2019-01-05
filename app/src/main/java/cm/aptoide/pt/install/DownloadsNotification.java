package cm.aptoide.pt.install;

import rx.Observable;

/**
 * Created by tiagopedrinho on 09/10/2018.
 */

public interface DownloadsNotification {

  Observable<String> handleOpenAppView();

  Observable<Void> handleOpenDownloadManager();

  void openAppView(String md5);

  void openDownloadManager();

  void setupNotification(String md5, String appName, int progress, boolean isIndeterminate);

  void removeNotificationAndStop();
}
