package cm.aptoide.pt.notification;

import cm.aptoide.pt.database.room.LocalNotificationSyncDao;
import cm.aptoide.pt.notification.sync.LocalNotificationSync;
import cm.aptoide.pt.sync.Sync;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.BackpressureStrategy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RoomLocalNotificationSyncPersistence implements LocalNotificationSyncPersistence {

  private final RoomLocalNotificationSyncMapper mapper;
  private final NotificationProvider provider;
  private final LocalNotificationSyncDao localNotificationSyncDao;

  public RoomLocalNotificationSyncPersistence(RoomLocalNotificationSyncMapper mapper,
      NotificationProvider provider, LocalNotificationSyncDao localNotificationSyncDao) {
    this.mapper = mapper;
    this.provider = provider;
    this.localNotificationSyncDao = localNotificationSyncDao;
  }

  @Override public void save(LocalNotificationSync notification) {
    localNotificationSyncDao.save(mapper.map(notification));
  }

  @Override public Observable<Sync> get(String id) {
    return RxJavaInterop.toV1Observable(localNotificationSyncDao.get(id),
        BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io())
        .onErrorReturn(throwable -> null)
        .map(sync -> {
          if (sync != null) {
            return mapper.map(sync, provider);
          } else {
            return null;
          }
        });
  }

  @Override public void remove(String id) {
    localNotificationSyncDao.delete(id);
  }
}
