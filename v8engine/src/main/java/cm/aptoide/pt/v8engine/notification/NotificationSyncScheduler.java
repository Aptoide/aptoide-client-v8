package cm.aptoide.pt.v8engine.notification;

public interface NotificationSyncScheduler {
  void schedule();

  void forceSync();

  void removeSchedules();

  void setEnabled(boolean enabled);
}
