package cm.aptoide.pt.sync;

public interface SyncScheduler {
  void schedule(Sync sync);

  void cancel(String syncId);

  void reschedule(Sync sync);
}
