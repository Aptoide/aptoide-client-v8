package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 03/05/2017.
 */

public class NotificationAccessor extends SimpleAccessor<Notification> {
  public NotificationAccessor(Database db) {
    super(db, Notification.class);
  }

  public Observable<List<Notification>> getDismissed(Integer[] notificationType, long startingTime,
      long endTime) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(Notification.class)
            .in("type", notificationType)
            .greaterThan("dismissed", startingTime)
            .lessThan("dismissed", endTime)
            .findAll()
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<Notification>> getAllSorted(Sort sortOrder, Integer[] notificationType) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(Notification.class)
            .in("type", notificationType)
            .findAllSorted("timeStamp", sortOrder)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Single<Notification> getLastShowed(Integer[] notificationType) {
    return getAllSorted(Sort.DESCENDING, notificationType).first()
        .map(notifications -> {
          if (notifications.isEmpty()) {
            return null;
          } else {
            return notifications.get(0);
          }
        })
        .toSingle();
  }

  public Observable<List<Notification>> getAllSorted(Sort sort) {
    return Observable.fromCallable(() -> database.get())
        .flatMap(realm -> realm.where(Notification.class)
            .findAllSorted("timeStamp", sort)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Completable deleteAllExcluding(List<String> ids) {
    return Completable.fromAction(
        () -> database.deleteAllExcluding(Notification.class, "ownerId", ids));
  }

  public Completable delete(String[] keys) {
    return Completable.fromAction(
        () -> database.deleteAllIn(Notification.class, Notification.KEY, keys));
  }
}