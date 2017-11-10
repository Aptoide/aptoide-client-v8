package cm.aptoide.pt.sync.rx;

import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.sync.Sync;
import cm.aptoide.pt.sync.SyncScheduler;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;

public class RxSyncScheduler implements SyncScheduler {

  private final Map<String, Subscription> subscriptionStorage;
  private final CrashLogger consoleLogger;

  public RxSyncScheduler(Map<String, Subscription> subscriptionStorage, CrashLogger consoleLogger) {
    this.subscriptionStorage = subscriptionStorage;
    this.consoleLogger = consoleLogger;
  }

  @Override public void schedule(Sync sync) {
    if (sync.isPeriodic()) {
      schedulePeriodicSync(sync);
    } else {
      scheduleOneOffSync(sync);
    }
  }

  @Override public void cancel(String syncId) {
    final Subscription subscription = subscriptionStorage.remove(syncId);

    if (subscription != null) {
      subscription.unsubscribe();
    }
  }

  @Override public void reschedule(Sync sync) {
    if (isSyncScheduled(sync.getId())) {
      cancel(sync.getId());
      schedule(sync);
    }
  }

  private void scheduleOneOffSync(Sync sync) {
    subscriptionStorage.put(sync.getId(), sync.execute()
        .subscribe(() -> subscriptionStorage.remove(sync.getId()),
            throwable -> consoleLogger.log(throwable)));
  }

  private void schedulePeriodicSync(Sync sync) {

    if (isSyncScheduled(sync.getId())) {
      return;
    }

    subscriptionStorage.put(sync.getId(),
        Observable.interval(sync.getTrigger(), sync.getInterval(), TimeUnit.MILLISECONDS)
            .flatMapCompletable(__ -> sync.execute()
                .doOnError(throwable -> consoleLogger.log(throwable))
                .onErrorComplete())
            .subscribe(__ -> {
            }, throwable -> {
              throw new OnErrorNotImplementedException(throwable);
            }));
  }

  private boolean isSyncScheduled(String syncId) {
    return subscriptionStorage.containsKey(syncId);
  }
}
