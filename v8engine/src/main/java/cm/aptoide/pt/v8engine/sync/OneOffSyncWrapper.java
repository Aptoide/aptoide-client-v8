package cm.aptoide.pt.v8engine.sync;

import rx.Completable;

public class OneOffSyncWrapper extends Sync {

  private final Sync sync;

  public OneOffSyncWrapper(Sync sync, long trigger) {
    super(sync.getId(), sync.isPeriodic(), sync.isExact(), trigger, 0);
    this.sync = sync;
  }

  @Override public Completable execute() {
    return sync.execute();
  }
}
