package cm.aptoide.pt.notification;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomNotificationPersistence;
import cm.aptoide.pt.database.room.RoomNotification;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 29/05/2017.
 */

public class NotificationsCleaner {

  public static final int MAX_NUMBER_NOTIFICATIONS_SAVED = 50;

  private final Calendar calendar;
  private final RoomNotificationPersistence roomNotificationPersistence;
  private final CompositeSubscription subscriptions;
  private AptoideAccountManager accountManager;
  private NotificationProvider notificationProvider;
  private CrashReport crashReport;

  public NotificationsCleaner(RoomNotificationPersistence roomNotificationPersistence,
      Calendar calendar, AptoideAccountManager accountManager,
      NotificationProvider notificationProvider, CrashReport crashReport) {
    this.roomNotificationPersistence = roomNotificationPersistence;
    this.calendar = calendar;
    this.accountManager = accountManager;
    this.notificationProvider = notificationProvider;
    this.crashReport = crashReport;
    subscriptions = new CompositeSubscription();
  }

  public void setup() {
    subscriptions.add(accountManager.accountStatus()
        .filter(account -> account.isLoggedIn())
        .flatMapCompletable(account -> cleanOtherUsersNotifications(account.getId()))
        .subscribe(notificationsCleaned -> {
        }, throwable -> crashReport.log(throwable)));

    subscriptions.add(notificationProvider.getNotifications(1)
        .flatMapCompletable(
            aptoideNotifications -> cleanLimitExceededNotifications(MAX_NUMBER_NOTIFICATIONS_SAVED))
        .subscribe(aptoideNotifications -> {
        }, throwable -> crashReport.log(throwable)));
  }

  public Completable cleanOtherUsersNotifications(String id) {
    List<String> idsList = new ArrayList<>(2);
    idsList.add(id);
    //where there is no login the account and the id is "" and those notifications should't be removed
    idsList.add("");
    return roomNotificationPersistence.deleteAllExcluding(idsList);
  }

  public Completable cleanLimitExceededNotifications(int limit) {
    return removeExpiredNotifications().andThen(removeExceededLimitNotifications(limit));
  }

  private Completable removeExpiredNotifications() {
    return Observable.defer(() -> roomNotificationPersistence.getAllSorted(Sort.DESCENDING))
        .first()
        .flatMapIterable(notifications -> notifications)
        .flatMap(notification -> {
          if (isNotificationExpired(notification)) {
            return Observable.just(notification);
          } else {
            return Observable.empty();
          }
        })
        .toList()
        .flatMapCompletable(notifications -> removeNotifications(notifications))
        .toCompletable();
  }

  private boolean isNotificationExpired(RoomNotification notification) {
    Long expire = notification.getExpire();
    if (expire != null) {
      long now = calendar.getTimeInMillis();
      return now > expire;
    }
    return false;
  }

  private Completable removeExceededLimitNotifications(int limit) {
    return Observable.defer(() -> roomNotificationPersistence.getAllSorted(Sort.DESCENDING))
        .first()
        .flatMapCompletable(notifications -> {
          if (notifications.size() > limit) {
            return removeNotifications(notifications.subList(limit, notifications.size()));
          } else {
            return Completable.complete();
          }
        })
        .toCompletable();
  }

  private Completable removeNotifications(List<RoomNotification> notifications) {
    return Observable.from(notifications)
        .map(notification -> notification.getKey())
        .toList()
        .filter(list -> !list.isEmpty())
        .flatMapCompletable(keys -> roomNotificationPersistence.delete(keys))
        .toCompletable();
  }
}
