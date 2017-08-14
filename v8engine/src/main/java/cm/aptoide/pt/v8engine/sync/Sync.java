package cm.aptoide.pt.v8engine.sync;

import rx.Completable;

public abstract class Sync {

  private final String id;
  private final boolean periodic;
  private final boolean exact;
  private final long trigger;
  private final long interval;

  public Sync(String id, boolean periodic, boolean exact, long trigger, long interval) {
    this.id = id;
    this.periodic = periodic;
    this.exact = exact;
    this.trigger = trigger;
    this.interval = interval;
  }

  public String getId() {
    return id;
  }

  public boolean isPeriodic() {
    return periodic;
  }

  public boolean isExact() {
    return exact;
  }

  public long getTrigger() {
    return trigger;
  }

  public long getInterval() {
    return interval;
  }

  public abstract Completable execute();
}
