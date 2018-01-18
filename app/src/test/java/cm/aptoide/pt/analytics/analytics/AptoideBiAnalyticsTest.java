package cm.aptoide.pt.analytics.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import rx.Completable;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AptoideBiAnalyticsTest {

  @Test public void logReachingThreshHold() throws Exception {
    AptoideBiEventService aptoideBiEventService = mock(AptoideBiEventService.class);
    CrashReport crashReport = mock(CrashReport.class);
    EventsPersistence eventPersistenceMock = mock(EventsPersistence.class);
    String eventName = "test event";
    String context = "test context";
    Map<String, Object> data = new HashMap<>();
    Event event = new Event(eventName, data, AnalyticsManager.Action.OPEN, context, 0);
    AptoideBiAnalytics analytics =
        new AptoideBiAnalytics(eventPersistenceMock, aptoideBiEventService,
            new CompositeSubscription(), crashReport, Schedulers.test(), 1, Integer.MAX_VALUE);
    when(aptoideBiEventService.send(any())).thenReturn(Completable.complete());
    List<Event> eventList = setupPersistence(eventPersistenceMock);

    analytics.setup();
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());

    verify(aptoideBiEventService).send(any());
    verify(eventPersistenceMock).save(Matchers.<Event>any());
    Assert.assertEquals(0, eventList.size());
  }

  @Test public void logReachingThreshHoldMultipleTimes() throws Exception {
    AptoideBiEventService aptoideBiEventService = mock(AptoideBiEventService.class);
    CrashReport crashReport = mock(CrashReport.class);
    EventsPersistence eventPersistenceMock = mock(EventsPersistence.class);
    String eventName = "test event";
    String context = "test context";
    Map<String, Object> data = new HashMap<>();
    Event event = new Event(eventName, data, AnalyticsManager.Action.OPEN, context, 0);
    AptoideBiAnalytics analytics =
        new AptoideBiAnalytics(eventPersistenceMock, aptoideBiEventService,
            new CompositeSubscription(), crashReport, Schedulers.test(), 2, Integer.MAX_VALUE);
    when(aptoideBiEventService.send(any())).thenReturn(Completable.complete());
    List<Event> eventList = setupPersistence(eventPersistenceMock);

    analytics.setup();
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());

    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());

    verify(aptoideBiEventService, times(2)).send(any());
    verify(eventPersistenceMock, times(4)).save(Matchers.<Event>any());
    Assert.assertEquals(0, eventList.size());
  }

  @Test public void logNotReachingThreshHold() throws Exception {
    AptoideBiEventService aptoideBiEventService = mock(AptoideBiEventService.class);
    CrashReport crashReport = mock(CrashReport.class);
    EventsPersistence eventPersistenceMock = mock(EventsPersistence.class);
    String eventName = "test event";
    String context = "test context";
    Map<String, Object> data = new HashMap<>();
    Event event = new Event(eventName, data, AnalyticsManager.Action.OPEN, context, 0);
    AptoideBiAnalytics analytics =
        new AptoideBiAnalytics(eventPersistenceMock, aptoideBiEventService,
            new CompositeSubscription(), crashReport, Schedulers.test(), 2, Integer.MAX_VALUE);
    when(aptoideBiEventService.send(any())).thenReturn(Completable.complete());
    List<Event> eventList = setupPersistence(eventPersistenceMock);

    analytics.setup();
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());

    verify(aptoideBiEventService, times(0)).send(eventList);
    verify(eventPersistenceMock, times(1)).save(any(Event.class));
    Assert.assertEquals(1, eventList.size());
  }

  @Test public void logTimeReached() throws Exception {
    AptoideBiEventService aptoideBiEventService = mock(AptoideBiEventService.class);
    CrashReport crashReport = mock(CrashReport.class);
    EventsPersistence eventPersistenceMock = mock(EventsPersistence.class);
    String eventName = "test event";
    String context = "test context";
    Map<String, Object> data = new HashMap<>();
    Event event = new Event(eventName, data, AnalyticsManager.Action.OPEN, context, 0);
    TestScheduler scheduler = Schedulers.test();
    AptoideBiAnalytics analytics =
        new AptoideBiAnalytics(eventPersistenceMock, aptoideBiEventService,
            new CompositeSubscription(), crashReport, scheduler, Integer.MAX_VALUE, 2);
    when(aptoideBiEventService.send(any())).thenReturn(Completable.complete());
    List<Event> eventList = setupPersistence(eventPersistenceMock);
    analytics.setup();
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());
    scheduler.advanceTimeBy(2, TimeUnit.SECONDS);

    verify(aptoideBiEventService).send(any());
    verify(eventPersistenceMock).save(Matchers.<Event>any());
    Assert.assertEquals(0, eventList.size());
  }

  @Test public void logNotTimeReached() throws Exception {
    AptoideBiEventService aptoideBiEventService = mock(AptoideBiEventService.class);
    CrashReport crashReport = mock(CrashReport.class);
    EventsPersistence eventPersistenceMock = mock(EventsPersistence.class);
    String eventName = "test event";
    String context = "test context";
    Map<String, Object> data = new HashMap<>();
    Event event = new Event(eventName, data, AnalyticsManager.Action.OPEN, context, 0);
    TestScheduler scheduler = Schedulers.test();
    AptoideBiAnalytics analytics =
        new AptoideBiAnalytics(eventPersistenceMock, aptoideBiEventService,
            new CompositeSubscription(), crashReport, scheduler, Integer.MAX_VALUE, 20000);
    when(aptoideBiEventService.send(any())).thenReturn(Completable.complete());
    List<Event> eventList = setupPersistence(eventPersistenceMock);
    analytics.setup();
    analytics.log(event.getEventName(), event.getData(), event.getAction(), event.getContext());
    scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

    verify(aptoideBiEventService, times(0)).send(any());
    verify(eventPersistenceMock, times(1)).save(Matchers.<Event>any());
    Assert.assertEquals(1, eventList.size());
  }

  @NonNull private List<Event> setupPersistence(EventsPersistence eventPersistenceMock) {
    //persistence start
    List<Event> eventList = new CopyOnWriteArrayList<>();
    PublishSubject<List<Event>> subject = PublishSubject.create();
    doAnswer(invocation -> Completable.fromAction(() -> {
      Object[] arguments = invocation.getArguments();
      if (arguments != null
          && arguments.length >= 1
          && arguments[0] != null
          && arguments[0] instanceof Event) {
        eventList.add(((Event) arguments[0]));
      }
      subject.onNext(new ArrayList<>(eventList));
    })).when(eventPersistenceMock)
        .save(Matchers.<Event>any());

    doAnswer(invocation -> Completable.fromAction(() -> {
      Object[] arguments = invocation.getArguments();
      if (arguments != null
          && arguments.length >= 1
          && arguments[0] != null
          && arguments[0] instanceof List
          && ((List) arguments[0]).size() > 0
          && ((List) arguments[0]).get(0) instanceof Event) {

        List<Event> events = (List<Event>) arguments[0];
        for (Event event1 : events) {
          eventList.remove(event1);
        }
        subject.onNext(new ArrayList<>(eventList));
      }
    })).when(eventPersistenceMock)
        .remove(any());
    //persistence end

    when(eventPersistenceMock.getAll()).thenReturn(subject.startWith(eventList));
    return eventList;
  }
}