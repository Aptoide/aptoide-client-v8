package cm.aptoide.pt.database;

import cm.aptoide.pt.database.room.NotificationDao;
import cm.aptoide.pt.database.room.RoomNotification;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import io.realm.Sort;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class RoomNotificationPersistence {

  private NotificationDao notificationDao;

  public RoomNotificationPersistence(NotificationDao notificationDao) {
    this.notificationDao = notificationDao;
  }

  public Single<List<RoomNotification>> getDismissed(Integer[] notificationType, long startingTime,
      long endTime) {
    return RxJavaInterop.toV1Single(
        notificationDao.getDismissed(notificationType, startingTime, endTime))
        .subscribeOn(Schedulers.io());
  }

  public Single<List<RoomNotification>> getAllSorted(Integer[] notificationType) {
    return RxJavaInterop.toV1Single(notificationDao.getAllSortedDescByType(notificationType))
        .subscribeOn(Schedulers.io());
  }

  public Single<RoomNotification> getLastShowed(Integer[] notificationType) {
    return getAllSorted(notificationType).map(notifications -> {
      for (RoomNotification notification : notifications) {
        if (!notification.isDismissed()) {
          return notification;
        }
      }
      return null;
    });
  }

  public Observable<List<RoomNotification>> getAllSorted(Sort sort) {
    return RxJavaInterop.toV1Observable(notificationDao.getAllSortedDesc(),
        BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Completable deleteAllExcluding(List<String> ids) {
    return Completable.fromAction(() -> notificationDao.deleteAllExcluding(ids))
        .subscribeOn(Schedulers.io());
  }

  public Completable delete(String[] keys) {
    return Completable.fromAction(() -> notificationDao.deleteByKey(keys))
        .subscribeOn(Schedulers.io());
  }

  public Observable<List<RoomNotification>> getAll() {
    return RxJavaInterop.toV1Observable(notificationDao.getAll(), BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io());
  }

  public Completable deleteAllOfType(int type) {
    return Completable.fromAction(() -> notificationDao.deleteAllByType(type))
        .subscribeOn(Schedulers.io());
  }

  public Completable insertAll(List<RoomNotification> notifications) {
    return Completable.fromAction(() -> notificationDao.insertAll(notifications))
        .subscribeOn(Schedulers.io());
  }

  public Completable insert(RoomNotification notification) {
    return Completable.fromAction(() -> notificationDao.insert(notification))
        .subscribeOn(Schedulers.io());
  }
}