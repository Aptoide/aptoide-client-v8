package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Sort;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 03/05/2017.
 */

public class NotificationAccessor extends SimpleAccessor<Notification> {
  NotificationAccessor(Database db) {
    super(db, Notification.class);
  }

  public Observable<List<Notification>> getAllSorted(Sort sortOrder, Integer[] notificationType,
      long startingTime, long endTime, boolean isShowed) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(Notification.class)
            .in("type", notificationType)
            .greaterThan("timeStamp", startingTime)
            .lessThan("timeStamp", endTime)
            .equalTo("showed", isShowed)
            .findAllSorted("timeStamp", sortOrder)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}