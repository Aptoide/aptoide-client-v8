package cm.aptoide.pt.analytics.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.subscriptions.CompositeSubscription;

public class AptoideBiAnalytics {

  private static final String TAG = AptoideBiAnalytics.class.getSimpleName();
  private final EventsPersistence persistence;
  private final AptoideBiEventService service;
  private final CompositeSubscription subscriptions;
  private final long sendInterval;
  private final Scheduler timerScheduler;
  private final long initialDelay;
  private final CrashReport crashReport;

  /**
   * @param sendInterval max time(in milliseconds) interval between event sends
   * @param crashReport
   */
  public AptoideBiAnalytics(EventsPersistence persistence, AptoideBiEventService service,
      CompositeSubscription subscriptions, Scheduler timerScheduler, long initialDelay,
      long sendInterval, CrashReport crashReport) {
    this.persistence = persistence;
    this.service = service;
    this.subscriptions = subscriptions;
    this.timerScheduler = timerScheduler;
    this.sendInterval = sendInterval;
    this.initialDelay = initialDelay;
    this.crashReport = crashReport;
  }

  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    persistence.save(new Event(eventName, data, action, context, System.currentTimeMillis()))
        .subscribe(() -> {
        }, throwable -> Logger.w(TAG, "cannot save the event due to " + throwable.getMessage()));
  }

  public void setup() {
    subscriptions.add(
        Observable.interval(initialDelay, sendInterval, TimeUnit.MILLISECONDS, timerScheduler)
            .flatMap(time -> persistence.getAll()
                .first())
            .filter(events -> events.size() > 0)
            .flatMapCompletable(events -> sendEvents(new ArrayList<>(events)))
            .doOnError(throwable -> crashReport.log(throwable))
            .retry()
            .subscribe());
  }

  @NonNull private Completable sendEvents(List<Event> events) {
    return persistence.remove(events)
        .toSingleDefault(events)
        .toObservable()
        .flatMapIterable(__ -> events)
        .map(event -> service.send(event)
            .toObservable()
            .flatMap(o -> Observable.empty())
            .cast(Event.class)
            .onErrorResumeNext(throwable -> Observable.just(event)))
        .toList()
        .flatMap(observables -> Observable.merge(observables))
        .toList()
        .filter(failedEvents -> !failedEvents.isEmpty())
        .flatMapCompletable(failedEvents -> persistence.save(failedEvents))
        .toCompletable();
  }
}
