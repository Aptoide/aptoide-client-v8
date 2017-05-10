package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.AptoideNotification;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.RealmQuery;
import io.realm.Sort;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 03/05/2017.
 */

public class NotificationAccessor extends SimpleAccessor<AptoideNotification> {
  NotificationAccessor(Database db) {
    super(db, AptoideNotification.class);
  }

  public Observable<List<AptoideNotification>> getAllSorted(Sort sortOrder,
      @AptoideNotification.NotificationType int notificationType) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> realm.where(AptoideNotification.class)
            .equalTo("type", notificationType)
            .findAllSorted("timeStamp", sortOrder)
            .asObservable())
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }

  public Observable<List<AptoideNotification>> getAllSorted(Sort sortOrder,
      @AptoideNotification.NotificationType Integer[] notificationType) {
    return Observable.fromCallable(() -> Database.getInternal())
        .flatMap(realm -> {
          RealmQuery<AptoideNotification> query = realm.where(AptoideNotification.class);
          query = query.in("type", notificationType);
          return query.findAllSorted("timeStamp", sortOrder).asObservable();
        })
        .unsubscribeOn(RealmSchedulers.getScheduler())
        .flatMap((data) -> database.copyFromRealm(data))
        .subscribeOn(RealmSchedulers.getScheduler())
        .observeOn(Schedulers.io());
  }
}
