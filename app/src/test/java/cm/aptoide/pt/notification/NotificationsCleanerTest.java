package cm.aptoide.pt.notification;

import android.support.annotation.NonNull;
import cm.aptoide.pt.database.accessors.NotificationAccessor;
import cm.aptoide.pt.database.realm.Notification;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import rx.Completable;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by trinkes on 28/08/2017.
 */
public class NotificationsCleanerTest {

  @Test public void cleanOtherUsersNotifications() throws Exception {

    Map<String, Notification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    Notification notification = createNotification(timeStamp + 1000, timeStamp, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 1000, timeStamp - 1, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 2, "me");
    list.put(notification.getKey(), notification);
    NotificationAccessor notificationAccessor = new NotAccessor(list);
    NotificationsCleaner notificationsCleaner = new NotificationsCleaner(notificationAccessor);

    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();
    notificationsCleaner.cleanOtherUsersNotifications("you")
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(notificationAccessor.getAllSorted(null)
        .toBlocking()
        .first()
        .size(), 0);
  }

  @Test public void cleanLimitExceededNotificationsWithAExpiredNotification() throws Exception {
    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();

    Map<String, Notification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    Notification notification = createNotification(0L, timeStamp, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 1000, timeStamp - 1, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 2, "me");
    list.put(notification.getKey(), notification);
    NotificationAccessor notificationAccessor = new NotAccessor(list);
    NotificationsCleaner notificationsCleaner = new NotificationsCleaner(notificationAccessor);

    notificationsCleaner.cleanLimitExceededNotifications(2)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(notificationAccessor.getAllSorted(null)
        .toBlocking()
        .first()
        .size(), 2);
  }

  @Test public void cleanLimitExceededNotificationsWithAExpiredNotificationAndExceedingLimit()
      throws Exception {

    Map<String, Notification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    Notification notification = createNotification(0L, timeStamp, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 1, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 2, "me");
    list.put(notification.getKey(), notification);
    NotificationAccessor notificationAccessor = new NotAccessor(list);
    NotificationsCleaner notificationsCleaner = new NotificationsCleaner(notificationAccessor);

    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();
    List<Notification> notificationList = notificationAccessor.getAllSorted(Sort.DESCENDING)
        .toBlocking()
        .first();
    notificationsCleaner.cleanLimitExceededNotifications(1)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(notificationAccessor.getAllSorted(null)
        .toBlocking()
        .first()
        .size(), 1);
    assertTrue(notificationAccessor.getAllSorted(Sort.DESCENDING)
        .toBlocking()
        .first()
        .get(0)
        .getKey()
        .equals(notificationList.get(1)
            .getKey()));
  }

  @Test public void cleanLimitExceededNotificationsExceedingLimit() throws Exception {
    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();

    Map<String, Notification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    Notification notification = createNotification(timeStamp + 2000, timeStamp, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 1, "me");
    list.put(notification.getKey(), notification);
    timeStamp = System.currentTimeMillis();
    notification = createNotification(timeStamp + 2000, timeStamp - 2, "me");
    list.put(notification.getKey(), notification);
    NotificationAccessor notificationAccessor = new NotAccessor(list);
    NotificationsCleaner notificationsCleaner = new NotificationsCleaner(notificationAccessor);

    List<Notification> notificationList = notificationAccessor.getAllSorted(Sort.DESCENDING)
        .toBlocking()
        .first();
    notificationsCleaner.cleanLimitExceededNotifications(1)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(notificationAccessor.getAllSorted(null)
        .toBlocking()
        .first()
        .size(), 1);
    assertTrue(notificationAccessor.getAllSorted(Sort.DESCENDING)
        .toBlocking()
        .first()
        .get(0)
        .getKey()
        .equals(notificationList.get(0)
            .getKey()));
  }

  @Test public void cleanLimitExceededNotificationsNotExpiring() throws Exception {
    TestSubscriber<Object> objectTestSubscriber = TestSubscriber.create();

    Map<String, Notification> list = new HashMap<>();
    long timeStamp = System.currentTimeMillis();
    Notification notification = createNotification(null, timeStamp, "me");
    list.put(notification.getKey(), notification);
    notification = createNotification(null, timeStamp - 1, "me");
    list.put(notification.getKey(), notification);
    notification = createNotification(null, timeStamp - 2, "me");
    list.put(notification.getKey(), notification);
    NotificationAccessor notificationAccessor = new NotAccessor(list);
    NotificationsCleaner notificationsCleaner = new NotificationsCleaner(notificationAccessor);

    notificationsCleaner.cleanLimitExceededNotifications(3)
        .subscribe(objectTestSubscriber);
    objectTestSubscriber.awaitTerminalEvent();
    objectTestSubscriber.assertCompleted();
    objectTestSubscriber.assertNoErrors();
    assertEquals(notificationAccessor.getAllSorted(null)
        .toBlocking()
        .first()
        .size(), 3);
  }

  @NonNull private Notification createNotification(Long expire, long timeStamp, String ownerId) {
    return new Notification(expire, null, null, 0, null, null, null, null, null, timeStamp, 0, 0,
        null, null, ownerId);
  }

  private class NotAccessor extends NotificationAccessor {

    private Map<String, Notification> list;

    public NotAccessor(Map<String, Notification> list) {
      super(null);
      this.list = list;
    }

    @Override public Observable<List<Notification>> getAllSorted(Sort sort) {
      ArrayList<Notification> value = new ArrayList<>(list.values());
      Collections.sort(value, (notification, t1) -> t1.getKey()
          .compareTo(notification.getKey()));
      return Observable.just(value);
    }

    @Override public Completable deleteAllExcluding(List<String> ids) {
      List<String> idsToRemove = new ArrayList<>();
      for (Notification notification : list.values()) {
        if (shouldRemove(notification, ids)) {
          idsToRemove.add(notification.getKey());
        }
      }
      return Observable.from(idsToRemove)
          .doOnNext(s -> list.remove(s))
          .toCompletable();
    }

    @Override public Completable delete(String[] keys) {
      return Completable.fromAction(() -> {
        for (String key : keys) {
          list.remove(key);
        }
      });
    }

    private boolean shouldRemove(Notification notification, List<String> ids) {
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