package cm.aptoide.pt.notification.sync;

import androidx.annotation.StringRes;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.sync.SyncScheduler;

public class LocalNotificationSyncManager {

  private static final long FIVE_MINUTES = 5 * 1000;

  private final SyncScheduler syncScheduler;
  private final NotificationProvider provider;
  private boolean enabled;

  public LocalNotificationSyncManager(SyncScheduler syncScheduler, boolean enabled,
      NotificationProvider provider) {
    this.syncScheduler = syncScheduler;
    this.enabled = enabled;
    this.provider = provider;
  }

  public void schedule(String title, String body, String image, @StringRes int actionStringString,
      String navigationUrl, String id, int type) {
    if (enabled) {
      syncScheduler.schedule(
          new LocalNotificationSync(provider, false, true, 0, FIVE_MINUTES, title, body, image,
              actionStringString, navigationUrl, id, type));
    }
  }

  public void unschedule(String type) {
    syncScheduler.cancel(type);
  }
}
