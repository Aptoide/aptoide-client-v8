package cm.aptoide.pt.notification;

import androidx.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomNotificationPersistence;
import cm.aptoide.pt.database.room.RoomNotification;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Completable;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

/**
 * Created by trinkes on 28/08/2017.
 */
public class NotificationsCleanerTest {

  @Test public void cleanOtherUsersNotifications() throws Exception {
    Map<String, RoomNotification> list = new HashMap<>();
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    long timeStamp = calendar.getTimeInMillis();
    RoomNotification notification = createNotification(timeStamp + 1000, timeStamp, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 1000, timeStamp - 1, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 2, "me", true);
    list.put(notification.getKey(), notification);
    RoomNotificationPersistence roomNotificationPersistence = new NotPersistenceRoom(list);

    NotificationsCleaner notificationsCleaner =
        new NotificationsCleaner(roomNotificationPersistence,
            Calendar.getInstance(TimeZone.getTimeZone("UTC")), getAptoideAccountManager(),
            getNotificationProvider(), CrashReport.getInstance());

    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();
    notificationsCleaner.cleanOtherUsersNotifications("you")
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .size(), 0);
  }

  @Test public void cleanLimitExceededNotificationsWithAExpiredNotification() throws Exception {
    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();

    Map<String, RoomNotification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    RoomNotification notification = createNotification(timeStamp, timeStamp, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 10000, timeStamp - 1000, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 20000, timeStamp - 2000, "me", true);
    list.put(notification.getKey(), notification);
    RoomNotificationPersistence roomNotificationPersistence = new NotPersistenceRoom(list);
    NotificationsCleaner notificationsCleaner =
        new NotificationsCleaner(roomNotificationPersistence,
            Calendar.getInstance(TimeZone.getTimeZone("UTC")), getAptoideAccountManager(),
            getNotificationProvider(), CrashReport.getInstance());

    notificationsCleaner.cleanLimitExceededNotifications(2)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(2, roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .size());
  }

  @Test public void cleanLimitExceededNotificationsWithAExpiredNotificationAndExceedingLimit()
      throws Exception {

    Map<String, RoomNotification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    RoomNotification notification = createNotification(0L, timeStamp, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 10000, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 20000, "me", true);
    list.put(notification.getKey(), notification);
    RoomNotificationPersistence roomNotificationPersistence = new NotPersistenceRoom(list);
    NotificationsCleaner notificationsCleaner =
        new NotificationsCleaner(roomNotificationPersistence,
            Calendar.getInstance(TimeZone.getTimeZone("UTC")), getAptoideAccountManager(),
            getNotificationProvider(), CrashReport.getInstance());

    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();
    List<RoomNotification> notificationList = roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first();
    notificationsCleaner.cleanLimitExceededNotifications(1)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .size(), 1);
    assertEquals(roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .get(0)
        .getKey(), notificationList.get(1)
        .getKey());
  }

  @Test public void cleanLimitExceededNotificationsExceedingLimit() throws Exception {
    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();

    Map<String, RoomNotification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    RoomNotification notification = createNotification(timeStamp + 2000, timeStamp, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 1, "me", true);
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 2, "me", true);
    list.put(notification.getKey(), notification);
    RoomNotificationPersistence roomNotificationPersistence = new NotPersistenceRoom(list);
    NotificationsCleaner notificationsCleaner =
        new NotificationsCleaner(roomNotificationPersistence,
            Calendar.getInstance(TimeZone.getTimeZone("UTC")), getAptoideAccountManager(),
            getNotificationProvider(), CrashReport.getInstance());

    List<RoomNotification> notificationList = roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first();
    notificationsCleaner.cleanLimitExceededNotifications(1)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .size(), 1);
    assertEquals(roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .get(0)
        .getKey(), notificationList.get(0)
        .getKey());
  }

  @Test public void cleanLimitExceededNotificationsNotExpiring() throws Exception {
    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();

    Map<String, RoomNotification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    RoomNotification notification = createNotification(null, timeStamp, "me", true);
    list.put(notification.getKey(), notification);
    notification = createNotification(null, timeStamp - 1, "me", true);
    list.put(notification.getKey(), notification);
    notification = createNotification(null, timeStamp - 2, "me", true);
    list.put(notification.getKey(), notification);
    RoomNotificationPersistence roomNotificationPersistence = new NotPersistenceRoom(list);
    NotificationsCleaner notificationsCleaner =
        new NotificationsCleaner(roomNotificationPersistence,
            Calendar.getInstance(TimeZone.getTimeZone("UTC")), getAptoideAccountManager(),
            getNotificationProvider(), CrashReport.getInstance());

    notificationsCleaner.cleanLimitExceededNotifications(3)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(roomNotificationPersistence.getAllSortedDesc()
        .toBlocking()
        .first()
        .size(), 3);
  }

  private NotificationProvider getNotificationProvider() {
    return Mockito.mock(NotificationProvider.class);
  }

  private AptoideAccountManager getAptoideAccountManager() {
    return Mockito.mock(AptoideAccountManager.class);
  }

  @NonNull private RoomNotification createNotification(Long expire, long timeStamp, String ownerId,
      boolean processed) {
    return new RoomNotification(expire, null, null, 0, null, null, null, null, null, null,
        timeStamp, 0, 0, null, null, ownerId, processed, 0, Collections.emptyList());
  }

  private class NotPersistenceRoom extends RoomNotificationPersistence {

    private Map<String, RoomNotification> list;

    public NotPersistenceRoom(Map<String, RoomNotification> list) {
      super(null);
      this.list = list;
    }

    @Override public Observable<List<RoomNotification>> getAllSortedDesc() {
      ArrayList<RoomNotification> value = new ArrayList<>(list.values());
      Collections.sort(value, (notification, t1) -> t1.getKey()
          .compareTo(notification.getKey()));
      return Observable.just(value);
    }

    @Override public Completable deleteAllExcluding(List<String> ids) {
      List<String> idsToRemove = new ArrayList<>();
      for (RoomNotification notification : list.values()) {
        if (shouldRemove(notification, ids)) {
          idsToRemove.add(notification.getKey());
        }
      }
      return Observable.from(idsToRemove)
          .doOnNext(s -> list.remove(s))
          .toCompletable();
    }

    @Override public Completable delete(List<String> keys) {
      return Completable.fromAction(() -> {
        for (String key : keys) {
          list.remove(key);
        }
      });
    }

    private boolean shouldRemove(RoomNotification notification, List<String> ids) {
      for (String id : ids) {
        if (notification.getOwnerId()
            .equals(id)) {
          return false;
        }
      }
      return true;
    }
  }
}
