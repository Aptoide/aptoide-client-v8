package cm.aptoide.analytics.implementation;

import android.support.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsLogger;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.data.Event;
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
  private final SessionPersistence sessionPersistence;
  private final AptoideBiEventService service;
  private final CompositeSubscription subscriptions;
  private final long sendInterval;
  private final Scheduler timerScheduler;
  private final long initialDelay;
  private final CrashLogger crashReport;
  private final AnalyticsLogger debugLogger;

  /**
   * @param sessionPersistence
   * @param sendInterval max time(in milliseconds) interval between event sends
   * @param debugLogger
   */
  public AptoideBiAnalytics(EventsPersistence persistence, SessionPersistence sessionPersistence,
      AptoideBiEventService service, CompositeSubscription subscriptions, Scheduler timerScheduler,
      long initialDelay, long sendInterval, CrashLogger crashReport, AnalyticsLogger debugLogger) {
    this.persistence = persistence;
    this.sessionPersistence = sessionPersistence;
    this.service = service;
    this.subscriptions = subscriptions;
    this.timerScheduler = timerScheduler;
    this.sendInterval = sendInterval;
    this.initialDelay = initialDelay;
    this.crashReport = crashReport;
    this.debugLogger = debugLogger;
  }

  public void log(String eventName, Map<String, Object> data, AnalyticsManager.Action action,
      String context) {
    persistence.save(new Event(eventName, data, action, context, System.currentTimeMillis()))
        .subscribe(() -> {
        }, throwable -> debugLogger.logWarningDebug(TAG,
            "cannot save the event due to " + throwable.getMessage()));
  }

  public long getTimestamp() {
    return sessionPersistence.getTimestamp();
  }

  public void saveTimestamp(long timestamp) {
    sessionPersistence.saveSessionTimestamp(timestamp);
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
