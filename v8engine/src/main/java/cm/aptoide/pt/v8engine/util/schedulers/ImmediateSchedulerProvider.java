package cm.aptoide.pt.v8engine.util.schedulers;

import android.support.annotation.NonNull;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Implementation of the {@link SchedulerProvider} making all {@link Scheduler}s immediate.
 */
public class ImmediateSchedulerProvider implements SchedulerProvider {
  @NonNull @Override public Scheduler computation() {
    return Schedulers.immediate();
  }

  @NonNull @Override public Scheduler io() {
    return Schedulers.immediate();
  }

  @NonNull @Override public Scheduler ui() {
    return Schedulers.immediate();
  }
}
