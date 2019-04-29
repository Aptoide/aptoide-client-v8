package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.sync.SyncScheduler;

public class LocalNotificationSyncManager {

  private final SyncScheduler syncScheduler;
  private final NotificationProvider provider;
  private boolean enabled;

  public LocalNotificationSyncManager(SyncScheduler syncScheduler, boolean enabled,
      NotificationProvider provider) {
    this.syncScheduler = syncScheduler;
    this.enabled = enabled;
    this.provider = provider;
  }

  public void schedule(String title, String body, String image, String navigationUrl, String id) {
    if (enabled) {
      syncScheduler.schedule(
          new LocalNotificationSync(provider, false, true, 0, 10000, title, body, image,
              navigationUrl, id));
    }
  }

  public void unschedule(String type) {
    syncScheduler.cancel(type);
  }
}
