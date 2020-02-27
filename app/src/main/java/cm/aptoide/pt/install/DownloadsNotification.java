package cm.aptoide.pt.install;

/**
 * Created by tiagopedrinho on 09/10/2018.
 */

public interface DownloadsNotification {

  void setupNotification(String md5, String appName, int progress, boolean isIndeterminate);

  void removeNotificationAndStop();
}
