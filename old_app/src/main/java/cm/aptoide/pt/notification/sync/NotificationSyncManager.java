package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.sync.SyncScheduler;

public class NotificationSyncManager implements NotificationSyncScheduler {

  private final SyncScheduler syncScheduler;
  private final NotificationSyncFactory notificationSyncFactory;
  private boolean enabled;

  public NotificationSyncManager(SyncScheduler syncScheduler, boolean enabled,
      NotificationSyncFactory notificationSyncFactory) {
    this.syncScheduler = syncScheduler;
    this.enabled = enabled;
    this.notificationSyncFactory = notificationSyncFactory;
  }

  @Override public void schedule() {
    if (enabled) {
      syncScheduler.schedule(notificationSyncFactory.create(
          NotificationSyncFactory.CAMPAIGN_NOTIFICATION_SYNC_PERIODIC));
    }
  }

  @Override public void forceSync() {
    if (enabled) {
      syncScheduler.schedule(notificationSyncFactory.create(
          NotificationSyncFactory.CAMPAIGN_NOTIFICATION_SYNC_IMMEDIATE));
    }
  }

  @Override public void removeSchedules() {
    syncScheduler.cancel(NotificationSyncFactory.CAMPAIGN_NOTIFICATION_SYNC_PERIODIC);
  }

  @Override public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
