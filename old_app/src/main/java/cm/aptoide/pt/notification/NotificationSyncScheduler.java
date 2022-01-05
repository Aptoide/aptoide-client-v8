package cm.aptoide.pt.notification;

public interface NotificationSyncScheduler {
  void schedule();

  void forceSync();

  void removeSchedules();

  void setEnabled(boolean enabled);
}
