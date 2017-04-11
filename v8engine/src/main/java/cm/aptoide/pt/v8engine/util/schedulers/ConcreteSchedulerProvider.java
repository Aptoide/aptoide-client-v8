package cm.aptoide.pt.v8engine.util.schedulers;

import android.support.annotation.NonNull;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Provides different types of schedulers.
 */
public class ConcreteSchedulerProvider implements SchedulerProvider {

  private static ConcreteSchedulerProvider INSTANCE;

  private ConcreteSchedulerProvider() {
  }

  public static synchronized ConcreteSchedulerProvider getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ConcreteSchedulerProvider();
    }
    return INSTANCE;
  }

  @NonNull @Override public Scheduler computation() {
    return Schedulers.computation();
  }

  @NonNull @Override public Scheduler io() {
    return Schedulers.io();
  }

  @NonNull @Override public Scheduler ui() {
    return AndroidSchedulers.mainThread();
  }
}
